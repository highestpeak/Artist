import requests
import re
import os


#爬取LOFTER上的摄影爱好者的摄影作品，大多数可以用，不排除少部分用不了
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

def page_url(url, page):
    url_0 = url
    page = int(page) + 1
    page_url_list = []
    num = 1
    while (num < page):
        url = url_0 + '?page=' + str(num)
        page_url_list.append(url)
        num += 1
    return page_url_list

def pic_url(page_url_list):
    pic_url_list = []
    for url in page_url_list:
        text = getHTML(url).text
        urls = re.findall('<img src="(.+?).jpg', text, re.S)
        name = re.findall('<a href="/">(.+?)</a>', text)
        for url in urls:
            portrait = re.search('imgsize', url)
            if not portrait:#试图过滤掉博主头像（？失败）LOFTER主页样式各不相同
                pic_url = url + '.jpg'
                pic_url_list.append(pic_url)
    print("博客主：" + name[-1])
    return pic_url_list, name[-1]


def download_pic(rootDoc, pic_url_list, name):
    num = 1
    for url in pic_url_list:
        try:
            #print(url)
            data = getHTML(url).content
            download_file_name = str(num) + '.jpg'
            path1 = rootDoc + str(name)
            path2 = rootDoc + str(name) + "/" + download_file_name
            if not os.path.exists(rootDoc):
                os.mkdir(rootDoc)
            if not os.path.exists(path1):
                os.mkdir(path1)
            if not os.path.exists(path2):
                with open(path2, 'wb') as f:
                    f.write(data)
                f.close()
                print("保存成功")
            else:
                print("文件已存在")
            print('Download pic ' + str(num) + ':')
            print(url)
            num += 1
        except:
            continue
    print('\n下载完成')


def main():
    url = "http://yimingarts.lofter.com/"
    rootDoc = "D://Pic/Lofter/"
    page = 10
    print('\n爬虫开始工作...\n')
    page_url_list = page_url(url, page)
    pic_url_list, name = pic_url(page_url_list)
    download_pic(rootDoc, pic_url_list, name)

if __name__ == "__main__":
    main()