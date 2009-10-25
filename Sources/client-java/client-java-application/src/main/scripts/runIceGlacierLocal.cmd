echo Start Goh Client!

if not "%JAVA_HOME%"=="" goto javaok
echo JAVA_HOME variable must be set to JDK installation directory!
goto end

:javaok

set APP_CLASSPATH=
for %%G in (./libs/*.jar) do (call :s_do_set %%G)

echo APP_CLASSPATH: %APP_CLASSPATH%

"%JAVA_HOME%/bin/java" -classpath config;%APP_CLASSPATH% %CLIENT_OPTIONS% -Djava.library.path=natives com.ospgames.goh.clientjava.IceClient --Ice.Config=config/ice-config-glacier-localhost.properties
goto :end

:s_do_set
set APP_CLASSPATH=%APP_CLASSPATH%;./libs/%1
GOTO :eof

:end