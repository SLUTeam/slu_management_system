
#Shell script for runnig our project because it contains some mandatory jar files.
set -e
mkdir -p bin
javac -cp "lib/*" -d bin $(find src -name "*.java")
MAIN_CLASS="LoginPage"
java -cp "bin:lib/*" "$MAIN_CLASS"
