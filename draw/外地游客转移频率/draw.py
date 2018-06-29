#coding=utf-8
import os,sys
from pylab import *
mpl.rcParams['font.sans-serif'] = ['SimHei'] #指定默认字体  
mpl.rcParams['axes.unicode_minus'] = False #解决保存图像是负号'-'显示为方块的问题  

parentdir=os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(parentdir)
import myutil.fileutil as fu
import myutil.myconfig as mc


import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

sns.set(font_scale=1.5,font='STSong')
flights_long = pd.read_csv(fu.getCurPathAndName('data.csv'), encoding="utf-8")
flights = flights_long.pivot(u"from", u"to", u"rate")

order=[u'故宫', u'天安门广场', u'天坛公园', u'后海', u'颐和园', u'鸟巢', u'王府井', u'雍和宫', u'国际机场', u'慕田峪长城']

flights = flights.reindex(index=order, columns=order)

#flights.index = pd.CategoricalIndex(flights.index, categories= )
#flights.sortlevel(level=0, inplace=True)

print flights

# 绘制x-y-z的热力图，比如 年-月-销量 的热力图
f, ax = plt.subplots(figsize=(9, 8))
#绘制热力图，还要将数值写到热力图上
#每个网格上用线隔开
#sns.heatmap(flights, annot=True, annot_kws={"size": 7})
sns.heatmap(flights, annot=True, fmt=".2f", linewidths=.1, ax=ax, cmap="YlOrRd")#, cbar=False)
#设置坐标字体方向
label_y = ax.get_yticklabels()
plt.setp(label_y, rotation=360, horizontalalignment='right')

label_x = ax.get_xticklabels()
plt.setp(label_x, rotation=30, horizontalalignment='right')
plt.show()
plt.tight_layout()
fig=plt.figure()
mc.saveFig(fig)