# JAVA_HOME points to JDK installation dir
if [ -z "$JAVA_HOME" ]
then
	echo JAVA_HOME variable must be set to JDK installation directory!
	echo Try /usr
	export JAVA_HOME=${java.home}
fi

unset TEST_CLIENT_CLASSPATH
TEST_CLIENT_CLASSPATH=./config:
for i in `ls ./libs`
do 
	TEST_CLIENT_CLASSPATH="$TEST_CLIENT_CLASSPATH:./libs/$i"
done

echo TEST_CLIENT_CLASSPATH: $TEST_CLIENT_CLASSPATH

$JAVA_HOME/bin/java -jar libs/${project.artifactId}-${project.version}.jar -classpath "$TEST_CLIENT_CLASSPATH" $1 $2 $3 $4 $5 $6 $7 $8 $9 $10 $11 $12 $13 $14 $15 $16

