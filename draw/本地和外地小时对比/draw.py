#coding=utf-8
import os,sys
from pylab import *
mpl.rcParams['font.sans-serif'] = ['SimHei'] #指定默认字体  
mpl.rcParams['axes.unicode_minus'] = False #解决保存图像是负号'-'显示为方块的问题  
import numpy as np
import matplotlib.pyplot as plt

parentdir=os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(parentdir)
import myutil.fileutil as fu
import myutil.myconfig as mc

labels=[u'本地居民', u'外地游客', u'时区修正', 'line4', 'line5', 'line6']

matrix=fu.getCurFileMatrix(mc.figure_input_file_name, ",")
#print matrix
plt.figure(figsize=(6, 5), dpi=mc.figure_dpi)
x=range(0, len(matrix[0]))
print x
for i in range(0, len(matrix)):
	plt.plot(x, matrix[i], mc.figure_line_styles[i],linewidth=1, label=labels[i])

plt.legend(loc='upper left', frameon=False)
xlabels=[str(i) for i in range(24)]
print xlabels
plt.xticks(x, xlabels, rotation=0)

plt.grid(True)
plt.xlabel(u'一天中的时间（小时）')
plt.ylabel(u'拍摄照片数')
plt.tight_layout()
mc.saveFig(plt)
plt.show()