import urllib
import urlparse
import HTMLParser
from urllib import urlopen
from bs4 import BeautifulSoup


if __name__ == "__main__":

##    url = "https://play.google.com/store/apps"
##    url = "http://www.google.com"
##    html = urllib.urlopen(url).read()
    
    html = open('test.html','r').readlines();
    html = ''.join(html)
    soup = BeautifulSoup(html)
    print "lenli",len(soup.findAll('li'))
    soup = soup.head.content[0]
    print len(soup)
    for ii in soup:
        print "!@#$%^&*(((((((((((()",ii
##    lis = soup.body.findAll('script',attrs={ 'class' : True }, limit=100)
##    print len(lis)
##    for li in lis:
##        print li['class']
        
    
##    f = open("test.html","w")
##    f.write(html)
##    f.close()
##    lis = soup.findAll('div')
##    print len(lis)
##    for li in lis:
##        print li['id']


