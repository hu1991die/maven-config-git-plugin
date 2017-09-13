## 1、配置文件git获取maven插件
    本插件为从指定git的url拉取配置文件，并存储在指定本地目录，供后续assembly插件进行打包使用
	
## 2、插件使用步骤
	注意: maven生命周期中同一个执行阶段 phase的 多个插件的执行顺序和代码的书写顺序有关，因为这个插件依赖后面的assembly 来copy配置文件，请保证这个插件的顺序在assembly插件前面。
	
### 3、项目配置插件引用
```

	<!-- 使用范例，如果是简单的项目，则可以直接在这里写死，如果需要灵活动态加载，则需要采用传参的方式传递进来 -->
    <properties>
		<gitUrl>http://xxxxxxxxx:xx/xxx/xxx.git</gitUrl>
		<gitUserName>xxxxxx</gitUserName>
		<gitPassword>xxxxxx</gitPassword>
		<gitLocalPath>/data/setting/order</gitLocalPath>
		<gitConfigPath>src/main/resources</gitConfigPath>
	</properties>

```

### 4、pom.xml文件中配置打包插件
```

	<plugin>
		<groupId>com.touna.loan</groupId>
		<artifactId>maven-config-git-plugin</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<configuration>
			<!-- 指定获取config的git url地址 -->
			<gitUrl>${gitUrl}</gitUrl>
			<!-- git用户名 -->
			<gitUserName>${gitUserName}</gitUserName>
			<!-- git密码 -->
			<gitPassword>${gitPassword}</gitPassword>
			<!-- git本地保存路径 -->
			<gitLocalPath>${gitLocalPath}</gitLocalPath>
			<!-- git项目 配置文件所在项目中的路径 -->
			<gitConfigPath>${gitConfigPath}</gitConfigPath>
		</configuration>
		<executions>  
			<execution>  
			   <!-- maven执行阶段  -->
				<phase>package</phase> 
				<goals>  
				<!-- maven执行目的 -->
					<goal>pullConfig</goal>  
				</goals>  
			</execution>  
		</executions>
	</plugin>

```
        
###  5、assembly打包配置新增拷贝配置文件路径
```

	<!-- ${gitLocalPath}/${gitConfigPath} maven配置的配置文件路径  -->
	<!--将项目中git拉去的配置文件下的内容放入package的第一级config-test目录中-->  
	<fileSet>
		<directory>${gitLocalPath}/${gitConfigPath}</directory>  
		<outputDirectory>conf-test</outputDirectory>   
		<includes>  
			<include>**.xml</include>
			<include>**.properties</include>
		    <include>**/*.xml</include>  
		    <include>**/*.properties</include>  
		    <include>**/*.conf</include>  
		</includes>  
		<fileMode>0644</fileMode>
	</fileSet> 

``` 

### 6、完整配置信息
```

	</profiles>
		<profile>
			<id>prod</id>
			<properties>
				<!-- 重新设置git配置文件目录 -->
				<configPath>${gitLocalPath}/${gitConfigPath}</configPath>
			</properties>
			<build>
				<plugins>
	
					<plugin>
						<groupId>com.touna.loan</groupId>
						<artifactId>maven-config-git-plugin</artifactId>
						<version>0.0.1-SNAPSHOT</version>
						<configuration>
							<!-- 指定获取config的git url地址 -->
							<gitUrl>${gitUrl}</gitUrl>
							<!-- git用户名 -->
							<gitUserName>${gitUserName}</gitUserName>
							<!-- git密码 -->
							<gitPassword>${gitPassword}</gitPassword>
							<!-- git本地保存路径 -->
							<gitLocalPath>${gitLocalPath}</gitLocalPath>
							<!-- git项目 配置文件所在项目中的路径 -->
							<gitConfigPath>${gitConfigPath}</gitConfigPath>
						</configuration>
						<executions>
							<execution>
								<!-- maven执行阶段 -->
								<phase>package</phase>
								<goals>
									<!-- maven执行目的 -->
									<goal>pullConfig</goal>
								</goals>
							</execution>
						</executions>
					</plugin>
	
					<!-- 打包jar文件时，配置manifest文件，加入lib包的jar依赖 -->
					<plugin>
						<groupId>org.apache.maven.plugins</groupId>
						<artifactId>maven-jar-plugin</artifactId>
						<configuration>
							<classesDirectory>target/classes/</classesDirectory>
							<archive>
								<manifest>
									<mainClass>com.touna.loan.settings.deploy.Bootstrap</mainClass>
									<!-- 打包时 MANIFEST.MF文件不记录的时间戳版本 -->
									<useUniqueVersions>false</useUniqueVersions>
									<addClasspath>true</addClasspath>
									<classpathPrefix>lib/</classpathPrefix>
								</manifest>
								<manifestEntries>
									<Class-Path>.</Class-Path>
								</manifestEntries>
							</archive>
							<excludes>
								<exclude>**/*.xml</exclude>
								<exclude>config/</exclude>
								<exclude>META-INF/</exclude>
							</excludes>
						</configuration>
					</plugin>
	
					<plugin>
						<artifactId>maven-dependency-plugin</artifactId>
						<executions>
							<execution>
								<id>unpack</id>
								<phase>package</phase>
								<goals>
									<goal>unpack-dependencies</goal>
								</goals>
								<configuration>
									<groupId>com.alibaba</groupId>
									<artifactId>dubbo</artifactId>
									<outputDirectory>${project.build.directory}/dubbo</outputDirectory>
									<includes>META-INF/assembly/**</includes>
								</configuration>
							</execution>
						</executions>
					</plugin>
	
					<plugin>
						<artifactId>maven-assembly-plugin</artifactId>
						<configuration>
							<finalName>system-setting-deploy</finalName>
							<descriptor>src/main/resources/assembly.xml</descriptor>
							<archive>
								<manifest>
									<mainClass>com.touna.loan.settings.deploy.Bootstrap</mainClass>
								</manifest>
							</archive>
						</configuration>
						<executions>
							<execution>
								<id>make-assembly</id>
								<phase>package</phase>
								<goals>
									<goal>single</goal>
								</goals>
							</execution>
						</executions>
					</plugin>
	
				</plugins>
			</build>
		</profile>
	</profiles>

```

### 7、执行maven打包
     mvn clean package