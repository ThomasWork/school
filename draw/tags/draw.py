#coding=utf-8
import os,sys
from pylab import *
#mpl.rcParams['font.sans-serif'] = ['SimHei'] #指定默认字体  
#mpl.rcParams['axes.unicode_minus'] = False #解决保存图像是负号'-'显示为方块的问题  

parentdir=os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(parentdir)
import myutil.fileutil as fu
import myutil.myconfig as mc

from wordcloud import WordCloud



def makeImage(text):
    #alice_mask = np.array(Image.open("alice_mask.png"))


    wc = WordCloud(background_color="white", max_words=10, font_path = r'G:\ASR\school\draw\tags\simfang.ttf')
    # generate word cloud
    wc.generate_from_frequencies(text)

    # show
    plt.imshow(wc, interpolation="bilinear")
    plt.axis("off")
    plt.show()

dict = fu.getCurFileDict(mc.figure_input_file_name, "%#####")

#dict = {'Alice': 2341, 'Beth': 9102, 'Cecil': 3258}

makeImage(dict)

# generate 可以对全部文本进行自动分词,但是他对中文支持不好,对中文的分词处理请看我的下一篇文章
#wordcloud = WordCloud(font_path = r'D:\Fonts\simkai.ttf').generate(f)
# 你可以通过font_path参数来设置字体集

#background_color参数为设置背景颜色,默认颜色为黑色


#!/usr/bin/env python
