import os,base64
import time

import numpy as np
import tensorflow as tf
from cv2 import cv2
from keras.applications.xception import (Xception, decode_predictions,
                                         preprocess_input)
from keras.models import load_model
from keras.preprocessing import image
from PIL import Image

dir_root = "/home/myflask/"
line_img_path = dir_root + "testImages/" + "gray.jpg"
one_img_path = dir_root + "testImages/" + "one.jpg"

# # 加载模型
mt1 = time.time()
model_line = load_model(dir_root + "models/line.h5")
graph_line = tf.get_default_graph()
model_classify = load_model(dir_root + "models/classify.h5")
graph_classify = tf.get_default_graph()
class_line = []
class_classify = []
with open(dir_root + "classfile/line.txt", 'r') as f:
    class_line = list(map(lambda x: x.strip(), f.readlines()))
with open(dir_root + "classfile/classify.txt", 'r') as f:
    class_classify = list(map(lambda x: x.strip(), f.readlines()))
mt2 = time.time()
print("加载模型: " + str(mt2-mt1) + "s")

# 读取图像，解决imread不能读取中文路径的问题
def cv_imread(file_path):
    cv_img = cv2.imdecode(np.fromfile(file_path,dtype=np.uint8),cv2.IMREAD_COLOR)
    return cv_img

def splitOnePic(imgOpen, orignH, orignW, partH, partW, numberH, numberW):
    intervalH = (orignH-partH)/(numberH-1)
    intervalW = (orignW-partW)/(numberW-1)
    num = 0
    for i in range(numberH):
        for j in range(numberW):
            x0 = int(j*intervalW)
            y0 = int(i*intervalH)
            x1 = int(x0+partW)
            y1 = int(y0+partH)
            cv2.imwrite(dir_root + "testImages/" + str(num) + ".jpg", imgOpen[y0:y1, x0:x1])
            num += 1

def canny(img):
    image_gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    r = 300.0 / image_gray.shape[1]
    dim = (300, int(image_gray.shape[0] * r))
    res = cv2.resize(image_gray, dim, interpolation=cv2.INTER_CUBIC)
    img1 = cv2.GaussianBlur(res, (3, 3), 0)
    canny = cv2.Canny(img1, 200, 250)
    cv2.imwrite(line_img_path, canny)

def prediction(model, graph, classes, img_path):
    img = image.load_img(img_path, target_size=(299, 299))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)
    # predict

    with graph.as_default():
        pred = model.predict(x)[0]
        result = [(classes[i], float(pred[i]) * 100.0) for i in range(len(pred))]
        result.sort(reverse=True, key=lambda x: x[1])

        (class_name, prob) = result[0]
        return class_name

def getScore(base):
    e0 = time.time()

    imgdata = base64.b64decode(base)
    file=open(one_img_path,'wb')
    file.write(imgdata)
    cv_img = cv_imread(one_img_path)

    canny(cv_img)
    h,w= cv_img.shape[:2]
    splitOnePic(cv_img, h, w, h*0.8, w*0.8, 3, 3)
    
    e1 = time.time()

    result = {'classify':'', 'score':'', 'best':'', 'line_score':''}
    best_id = ''
    best_score = 0
    for i in range(9):
        img_path = dir_root + "testImages/" + str(i) + ".jpg"
        classify = prediction(model_classify, graph_classify, class_classify, img_path)
        s = classify.split('_')
        if i==4:
            result['classify'] = s[0]
            result['score'] = s[1]
        if int(s[1]) > best_score:
            best_id = str(i)
            best_score = int(s[1])
    result['best'] = best_id

    e2 = time.time()

    result['line_score'] = prediction(model_line, graph_line, class_line, line_img_path)

    e3 = time.time()
    print("切割和线条化: " + str(e1-e0) + "s")
    print("分类评分: " + str(e2-e1) + "s")
    print("线条评分: " + str(e3-e2) + "s")

    return result

def getOneScore(base):
    imgdata = base64.b64decode(base)
    file=open(one_img_path,'wb')
    file.write(imgdata)
    
    cv_img = cv_imread(one_img_path)

    canny(cv_img)

    result = {'classify':'', 'score':'', 'line_score':''}
    classify = prediction(model_classify, graph_classify, class_classify, one_img_path)
    s = classify.split('_')
    result['classify'] = s[0]
    result['score'] = s[1]
    result['line_score'] = prediction(model_line, graph_line, class_line, line_img_path)
    return result

# content_pic_path = dir_root + 'testImages/content.jpg'
# style_pic_path = dir_root + 'testImages/style.jpg'
# result_pic_path = dir_root + 'testImages/'
# py_file = dir_root + 'Neural-Style-Transfer-master/Network.py'
# def style_transfer(phote_base,style_base):
#     imgdata1 = base64.b64decode(phote_base)
#     file1=open(content_pic_path,'wb')
#     file1.write(imgdata1)

#     imgdata2 = base64.b64decode(style_base)
#     file2=open(style_pic_path,'wb')
#     file2.write(imgdata2)

#     os.system('python3 ' + py_file + ' ' + content_pic_path + ' ' + style_pic_path + ' ' + result_pic_path)

#     base64_data = ''
#     with open(result_pic_path + 'result.jpg',"rb") as f:
#         base64_data = base64.b64encode(f.read())

#     json = {'result':str(base64_data,'utf8')}
#     return json