import requests
import os
from bs4 import BeautifulSoup
import re


#爬取指定百家号id的页面中的图片及描述
headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.89 "
                  "Safari/537.36"
}

def getHTML(url, code='utf-8'):
    try:
        r = requests.get(url, headers=headers)
        r.raise_for_status()
        r.encoding = code
        return r
    except:
        return "访问失败"

def getPicture(url):
    links = []
    names = []
    pic = getHTML(url)
    soup = BeautifulSoup(pic.text, "html.parser")
    count = 0
    for img in soup.find_all('div', class_='img-container'):
        #name = re.sub(r'<.*?>', '', str(img.previousSibling.previousSibling) + str(img.previousSibling))
        name = re.sub(r'<.*?>', '', str(img.nextSibling) + str(img.nextSibling.nextSibling))
        #name = "金奖-" + str(count)
        names.append(name)
        #count = count + 1
        for i in img.find_all('img'):
            try:
                src = i.attrs['src']
                links.append(src)
            except:
                continue

    print(names)
    '''
    print(len(names))
    print(links)
    print(len(links))
    '''
    return links, names

def savePicture(links, names, root):
    try:
        if not os.path.exists(root):
            os.mkdir(root)
        for i in range(len(links)):
            if names[i]:
                path = root + names[i] + ".JPEG"
            else:
                path = root + links[i].split("/")[-1]
            if not os.path.exists(path):
                with open(path, 'wb') as f:
                    f.write(getHTML(links[i]).content)
                f.close()
                print("保存成功")
            else:
                print("文件已存在")
                #print(path)
    except:
        print("爬取失败")

def main():
    #在url中的"id"处手动修改合适的百家号id，注意不同文章对照片的描述在不同位置\
    #需要修改getPicture(url)中name的获取
    url = "https://baijiahao.baidu.com/s?id=1614174046658831112&wfr=spider&for=pc"
    rootDoc = "D://Pic/baijiahao/"
    links, names = getPicture(url)
    savePicture(links, names, rootDoc)

if __name__ == "__main__":
    main()