echo Start Goh Server!

if not "%JAVA_HOME%"=="" goto javaok
echo JAVA_HOME variable must be set to JDK installation directory!
goto end

:javaok

set SERVER_CLASSPATH=
for %%G in (./libs/*.jar) do (call :s_do_set %%G)

echo SERVER_CLASSPATH: %SERVER_CLASSPATH%

"%JAVA_HOME%/bin/java" -jar libs/${project.artifactId}-${project.version}.jar -classpath ./config;%SERVER_CLASSPATH% %SERVER_OPTIONS% --Ice.Config=config/ice-config.properties %1 %2 %3 %4 %5 %6 %7 %8 %9 %10 %11 %12 %13 %14 %15 %16
goto :end

:s_do_set
set SERVER_CLASSPATH=%SERVER_CLASSPATH%;./libs/%1
GOTO :eof

:end