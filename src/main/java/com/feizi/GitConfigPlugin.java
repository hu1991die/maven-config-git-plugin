package com.feizi;

import com.feizi.utils.StringUtils;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

/**
 * Created by feizi Ruan on 2017/9/13.
 * Maven-Git插件
 */
public class GitConfigPlugin extends AbstractMojo {
    /*git地址*/
    private String gitUrl;
    /*git用户名*/
    private String gitUserName;
    /*git密码*/
    private String gitPassword;
    /*本地配置文件存储路径*/
    private String gitLocalPath;
    /*项目中的配置文件目录*/
    private File gitConfigPath;

    /**
     * 下载配置文件到指定的项目目录
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        /*1,参数校验*/
        boolean result = checkParam();
        if(result){
            /*2、远程拉取配置文件*/
            pullFromRemote();
        }else {
            getLog().error("参数验证失败..");
            System.exit(0);
        }
    }

    /**
     * @Desc: 从git远程仓库拉取配置文件到本地目录
     * 每次maven编译打包的时候，首先会删除原有旧的配置文件信息，
     * 然后直接从git远程仓库clone最新的配置文件
     * @param: 
     * @return 
     * @author: feizi Ruan
     * @date:20:13 2017/9/13
     */
    private void pullFromRemote(){
        try {
            /*创建一个新的文件夹用于存放git clone下来的最新配置文件*/
            File localPath = new File(gitLocalPath);
            getLog().info("本地git配置目录" + gitLocalPath);

            /*如果原先存在，则直接删除掉，首次不存在*/
            if(localPath.exists() && localPath.isDirectory()){
                deleteFolder(localPath);
                getLog().info("本地git目录以及存在，delete...");
            }

            localPath.mkdir();
            getLog().info("gitUrl = " + gitUrl + " ，localPath = " + localPath);

            CloneCommand clone = Git.cloneRepository().setURI(gitUrl).setDirectory(localPath);
            if(gitUrl.contains("http") || gitUrl.contains("https")){
                UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(gitUserName, gitPassword);
                clone.setCredentialsProvider(credentialsProvider);
            }

            /*git clone配置文件到指定的目录*/
            clone.call();
        } catch (GitAPIException e) {
            getLog().error(e);
            System.exit(0);
        }
    }

    /**
     * @Desc: 校验参数
     * @param:
     * @return 
     * @author: feizi Ruan
     * @date:20:03 2017/9/13
     */
    private boolean checkParam(){
        if(StringUtils.isBlank(gitUrl)){
            getLog().info("gitUrl 参数未赋值..");
            return false;
        }
        if (StringUtils.isBlank(gitUserName)) {
            getLog().info("gitUserName 参数未赋值..");
            return false;
        }

        if (StringUtils.isBlank(gitPassword)) {
            getLog().info("gitPassword 参数未赋值..");
            return false;
        }

        if (StringUtils.isBlank(gitLocalPath)) {
            getLog().info("gitLocalPath 参数未赋值..");
            return false;
        }
        if (gitConfigPath == null || "".equals(gitConfigPath)) {
            getLog().info("gitConfigPath 参数未赋值..");
            return false;
        }
        return true;
    }

    /**
     * @Desc: 删除文件
     * @param: file 要删除的文件
     * @return 
     * @author: feizi Ruan
     * @date:20:07 2017/9/13
     */
    private void deleteFolder(File file){
        if(null != file && (file.isFile() || file.list().length == 0)){
            file.delete();
        }else{
            File[] files = file.listFiles();
            if(null != files && files.length > 0){
                for (File f : files){
                    deleteFolder(f);
                    f.delete();
                }
            }
        }
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getGitUserName() {
        return gitUserName;
    }

    public void setGitUserName(String gitUserName) {
        this.gitUserName = gitUserName;
    }

    public String getGitPassword() {
        return gitPassword;
    }

    public void setGitPassword(String gitPassword) {
        this.gitPassword = gitPassword;
    }

    public String getGitLocalPath() {
        return gitLocalPath;
    }

    public void setGitLocalPath(String gitLocalPath) {
        this.gitLocalPath = gitLocalPath;
    }

    public File getGitConfigPath() {
        return gitConfigPath;
    }

    public void setGitConfigPath(File gitConfigPath) {
        this.gitConfigPath = gitConfigPath;
    }
}
