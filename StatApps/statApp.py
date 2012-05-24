import urllib
import urlparse
import HTMLParser
from urllib import urlopen
import re
import sys


class Permission:
    group_title = ''
    description = list()
    description_full = list()
    
class App:
    name = '<none>'
    url = ''
    price = 0.0
    isFree = True
    permissions = list()
    
class Category:
    name = '<none>'
    url = ''
    content = ''
    paid_seemore_url = ''
    free_seemore_url = ''
    apps = list()
    
    def __init__(self):
        pass

def url_content(url):
    return ''.join(urllib.urlopen(url).read())

def cnstr(string):
    return string.decode('UTF-8').encode(encoding_type)

def get_next_url(cur_url):
    st = re.search('start=(\d+?)&',cur_url) 
    if st == None:
        cur_url += '?start=0&num=24'
        st = 0
    else:
        st = int(st.group(1))
    st = st + 24
    cur_url = cur_url[: cur_url.find('?') ] # remove '?start=xx&num=24'
    cur_url = cur_url + '?start=%d&num=24' %(st)
    print "get_next_url", cur_url
    return cur_url

debug = False
if __name__ == "__main__":
    sorry_content = """but your computer or network may be sending automated queries. To protect our users, we can't process your request right now."""

    
    encoding_type = sys.getfilesystemencoding()
    
    content = open('test.html','r')
    content = ''.join(content)
    pattern = re.compile(r'<li class="category-item "><a href="(.+?)">(.+?)</a>')
    categorys = pattern.findall(content)
    googleapp = "https://play.google.com"
    ## LBS app counter
    lbscount = 0
    for cate in categorys:
    #if categorys:
        #one_cate = categorys[0]
        one_cate = cate
        cate = one_cate[0]
        cur_cate = Category()
        cate_name = one_cate[1] #re.search(r'', cate)
        cur_cate.url = googleapp + cate
        cur_cate.content = url_content(cur_cate.url)
        if cate_name:
            cur_cate.name = cate_name #.groups()[0]
            if debug:
                print "Category:",cur_cate.name
            seemore = re.findall(r'<div class="carousel-more-link"><a href="(.+?)">',
                      cur_cate.content)
##            print "seemore:",seemore
            if seemore:
                cur_cate.free_seemore_url = googleapp + seemore[0]
                while True:
                    if debug:
                        print "\t"*2, "seemore_url", cur_cate.free_seemore_url
                    free_content = url_content(cur_cate.free_seemore_url)
                    # out of page
                    if -1 != free_content.find('the requested URL was not found on this server.')\
                       or -1 != free_content.find(sorry_content):
                        break
##                    if -1 != cur_cate.free_seemore_url.find('start=96'):
##                        print free_content
                    
                    app_list = re.findall(
                        r'<a class="title" title="(?P<title>.+?)".+?href="(?P<url>.+?)"',
                        free_content)
                    print "applist: ",len(app_list)
                    for each_app in app_list:
                    ##if app_list:
                    ##    each_app = app_list[0]
                        app = App()
                        app.name = each_app[0]
                        print "\t"*1,"app name:", cnstr(app.name)
                        app.url = googleapp + each_app[1]
                        app_content = url_content(app.url)
                        button_price = re.findall(
                            r'<span class="buy-button-price".+?>(.+?)</span>',
                            app_content)
                        if button_price != None:
##                          print "\t"*2,"button:",button_price[0].decode('UTF-8').encode(encoding_type)
                            price = re.search(r'\d+\.\d+', button_price[0])
    ##                      if button_price[0] == "Install" or button_price[0] == "\xe5\xae\x89\xe8\xa3\x85":
                            if price == None:
                                app.price = 0.
                                app.isFree = True
                            else:
                                price = price.group(0)
                                print "price:", price
                                app.price = float(price)
                                app.isFree = False
                        permission_groups = re.findall(
                            r'<li class="doc-permission-group">(.+?)</li>',
                            app_content)
                        for permission_group in permission_groups:
    ##                    if permission_groups:
    ##                        permission_group = permission_groups[0]
                            permission = Permission()
                            permission.group_title = re.search(
                                r'<span class="doc-permission-group-title">(.+?)</span>',
                                permission_group)
                            permission.group_title = cnstr(permission.group_title.group(1))
                            
                            permission.description = re.findall(
                                r'<div class="doc-permission-description">(.+?)</div>',
                                permission_group)
                            
                            permission.description_full = re.findall(
                                r'<div class="doc-permission-description-full">(.+?)</div>',
                                permission_group)
                            
                            if debug:
                                print "\t"*2,"title:",permission.group_title
                            for j in range(len(permission.description)):
##                                if cnstr("精准的 (GPS) 位置)") == cnstr(permission.description[j]):
                                if debug:
                                    print "\t"*3,"description:",j,cnstr(permission.description[j])
                                    print "\t"*3,"full text:",j,cnstr(permission.description_full[j])
                        cur_cate.apps.append(app)
                    # for each_app end
                    print "for app end"
                    cur_cate.free_seemore_url = get_next_url(cur_cate.free_seemore_url)
                ##while True
                
        print "cur_cate apps len: ",len(cur_cate.apps)
        
        for app in cur_cate.apps:
            for per in app.permissions:
                if cnstr(per.group_title) == cnstr("精准的 (GPS) 位置"):
                    lbscount += 1
        
                
## it seem paid disappear?
##                if len(seemore) == 2:
##                    cur_cate.paid_seemore_url = googleapp + seemore[1]
##                print cur_cate.paid_seemore_url
##                paid_content = url_content(cur_cate.paid_seemore_url)
                
    
##    parser = ParseLinks()
####    parser.start("https://play.google.com/store/apps")
##    parser.start("http://www.cplusplus.com/reference/")
    
##    content = unicode(urllib.urlopen('http://www.ip138.com/ips8.asp').read(), 'GB2312')
##    parser = CustomParser()
##    parser.feed(content)
##    parser.close()





##    class CustomParser(HTMLParser.HTMLParser):
##    selected = ('table', 'h1', 'font', 'ul', 'li', 'tr', 'td', 'a')
##    
##    def reset(self):
##        HTMLParser.HTMLParser.reset(self)
##        self._level_stack = []
##    def handle_starttag(self, tag, attrs):
##        if tag in CustomParser.selected:
##            self._level_stack.append(tag)
##    def handle_endtag(self, tag):
##        if self._level_stack \
##        and tag in CustomParser.selected \
##        and tag == self._level_stack[-1]:
##            self._level_stack.pop()
##    def handle_data(self, data):
##        if "/".join(self._level_stack) in (
##            'table/tr/td'):
##            print "============ddd========"
##            print "/".join(self._level_stack)
##            print "=============sssss========"
##            print self._level_stack, data
##            
