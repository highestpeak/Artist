from flask import Flask, render_template, request
from keras.preprocessing import image

import testModel as func

app = Flask(__name__)

@app.route('/')
def home():
    return render_template('home.html')

# json example， {"best":"0","classify":"thing","line_score":"51","score":"50"}
@app.route('/up_photo',methods=['post'])
def up_photo():
    file = request.form.get('photo')
    json = func.getScore(file)
    return json

# json example， {"classify":"thing","line_score":"51","score":"50"}
@app.route('/up_one_photo',methods=['post'])
def up_one_photo():
    file = request.form.get('photo')
    json = func.getOneScore(file)
    return json

@app.route('/style_transfer',methods=['post'])
def style_transfer():
    photo = request.form.get('photo')
    style = request.form.get('style')
    json = func.style_transfer(photo,style)
    return json

if __name__ == "__main__":
    app.run(host='0.0.0.0',port='80')