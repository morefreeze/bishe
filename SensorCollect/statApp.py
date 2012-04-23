import urllib
import urlparse
import HTMLParser
from urllib import urlopen
from bs4 import BeautifulSoup

class ParseLinks(HTMLParser.HTMLParser):
    
    def start(self,url):
        self._url = url
        self.feed(urllib.urlopen(url).read())


    def handle_starttag(self, tag, attrs):
        if tag == "img":
            isDoodle = False
            for name,value in attrs:
                print name,value
                if name == "alt" and value == "Google":
                    isDoodle = True
                if isDoodle and name == "src":
                    print "saving",value
                    data = urlopen(self._url+"/"+value).read()
                    f = open("doodle.png","wb")
                    f.write(data)
                    f.close()

parser = ParseLinks()
parser.start("http://www.google.com")
