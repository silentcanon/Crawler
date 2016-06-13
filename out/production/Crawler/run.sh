echo "Now start to crawling..."
java -Djava.ext.dirs=.  com.canon.webcrawler.test.Main
mv data.json dataWithGroup.json ./static
echo "Succeed! Please open index.html for visulization!"

