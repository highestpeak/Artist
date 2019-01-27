import requests
import os
from bs4 import BeautifulSoup
import re


#爬取全球摄影网中的获奖作品
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

def getPageURL(rootURL):
    soup = BeautifulSoup(getHTMLText(rootURL).text, "html.parser")
    pageNum = []
    page = soup.find_all('a')
    for p in page:
        try:
            href = p.attrs['href']
            r_href = re.findall(r'/winningworks/.+\.html', href)
            if r_href:
                pageNum.append(rootURL + r_href[0])
        except:
            continue
    return list(set(pageNum))

def getPicture(url, rootURL):
    link = []
    pic = getHTMLText(url)
    soup = BeautifulSoup(pic.text, "html.parser")
    for img in soup.find_all('div', class_='container-fluid'):
        for i in img.find_all('img'):
            try:
                src = rootURL + i.attrs['src']
                link.append(src)
            except:
                continue
    return link

def savePicture(link, root):
    try:
        if not os.path.exists(root):
            os.mkdir(root)
        for l in link:
            path = root + l.split("/")[-1]
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
    rootURL = "http://www.g-photography.net"
    rootDoc = "D://Pic/GlobalPhotography/"

    url = getPageURL(rootURL)
    for u in url:
        link = getPicture(u, rootURL)
        savePicture(link, rootDoc)

if __name__ == "__main__":
    main()