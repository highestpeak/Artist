import requests
import os
from bs4 import BeautifulSoup
import re


#爬取 中国国家地理->景观 中的所有摄影作品缩略图，按网站上的分类分别存储至同名文件夹，一共2268张图片
headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.89 "
                  "Safari/537.36"
}

def getHTMLText(url, code='utf-8'):
    try:
        r = requests.get(url, headers=headers)
        r.raise_for_status()
        r.encoding = code
        return r
    except:
        return "访问失败"

#获得每一个类别名称以作为文件夹名，以及类别下的子类的url
def getKindURL(rootURL, rootDoc):
    soup = BeautifulSoup(getHTMLText(rootURL).text, "html.parser")
    place = soup.find_all('ul', class_='lvxingdi-list')
    placeName = re.findall(r'2>[\u4e00-\u9fa5]{2,7}', str(place))
    fpath = []
    for p in placeName:
        fpath.append(rootDoc + p[2:])
    #print(fpath)
    id = re.findall(r'\d{5}', str(place))
    urlOfOneKind = []
    urlOfAll = []
    count = 0
    for i in id:
        urlOfOneKind.append(rootURL + i)
        count = count + 1
        if count == 20 or count == 31 or count == 40 or count == 46 or count == 68\
                or count == 84 or count == 93 or count == 102:
            urlOfAll.append(urlOfOneKind)
            urlOfOneKind = []
    #print(url)
    return urlOfAll, fpath

#得到每个小类有多少页并获得每一页的url
def getPageURL(rootURL):
    soup = BeautifulSoup(getHTMLText(rootURL).text, "html.parser")
    pageNum = []
    page = soup.find_all('a')
    for p in page:
        try:
            href = p.attrs['href']
            r_href = re.findall(r'/\d\.htm', href)
            if r_href:
                pageNum.append(r_href)
        except:
            continue
    if pageNum:
        pageNum.pop()
    pageNum.insert(0, ['/1.htm'])
    url = []
    for pn in pageNum:
        pn = "".join(pn)
        url.append(rootURL + pn)
    return url

#得到图片链接
def getPicture(url):
    link = []
    pic = getHTMLText(url)
    soup = BeautifulSoup(pic.text, "html.parser")
    for img in soup.find_all('div', class_='thumb'):
        i = img.find('div', class_='thumb-img').find('img')
        link.append(i.get('src'))

    return link

def savePicture(link, root):
    try:
        if not os.path.exists(root):
            os.mkdir(root)
        for l in link:
            #print(l)
            path = root + "/" + l.split('/')[-1][:-5]
            if not os.path.exists(path):
                with open(path, 'wb') as f:
                    f.write(getHTMLText(l).content)
                f.close()
                print("保存成功")
            else:
                print("文件已存在")
                #print(path)
    except:
        print("爬取失败")

def main():
    rootURL = "http://www.dili360.com/travel/sight/"
    rootDoc = "D://Pic/dili360/"
    url, fpath = getKindURL(rootURL, rootDoc)

    count = 0
    for a_kind in url:
        for a in a_kind:
            a = getPageURL(a)
            for a_i in a:
                a_i = getPicture(a_i)
                savePicture(a_i, fpath[count])
                #print("\r%d" % count, end="")
        count = count + 1

if __name__ == "__main__":
    main()