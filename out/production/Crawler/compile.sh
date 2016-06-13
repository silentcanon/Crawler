echo "Start to compiling..."
javac -Djava.ext.dirs=. com/canon/webcrawler/net/*.java
javac -Djava.ext.dirs=. com/canon/webcrawler/utils/*.java
javac -Djava.ext.dirs=. com/canon/webcrawler/crawler/*.java
javac -Djava.ext.dirs=. com/canon/webcrawler/test/*.java
echo "Succeed!"
