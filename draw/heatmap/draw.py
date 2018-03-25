#coding=utf-8
import os,sys
from pylab import *
mpl.rcParams['font.sans-serif'] = ['SimHei'] #指定默认字体  
mpl.rcParams['axes.unicode_minus'] = False #解决保存图像是负号'-'显示为方块的问题  

parentdir=os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(parentdir)
import myutil.fileutil as fu
import myutil.myconfig as mc
#plt.rc('text', usetex=True)
import heatmap
import random
pts = [(random.random(), random.random()) for x in range(50)]
#print pts

pathAndName=fu.getCurPathAndName('39.4_41.6.txt')
b=fu.getFileTupleList(pathAndName, ',')
#print b.len()
hm = heatmap.Heatmap()
#下边小，上边大
#左边小，右边大，和地图上的经纬度一致
#hm.heatmap(b, dotsize=1000, size=(1024, 1024), area=((1, 120), (2, 140))).save("C:/Users/Admin/Desktop/classic.png")
#hm.heatmap(b, size=(1024, 1024)).save("C:/Users/Admin/Desktop/classic.png")
hm.heatmap(b, dotsize=16, size=(100, 100), area=((115.7, 39.4), (117.4, 41.6)), scheme='classic').save("C:/Users/Admin/Desktop/classic.png")

#hm = heatmap.Heatmap()
#pts = [(random.uniform(30.012, 40.050), random.uniform(110, 130)) for x in range(100)]
#img = hm.heatmap(pts)
#img.save("C://data.png")
hm.saveKML("C://data.kml")