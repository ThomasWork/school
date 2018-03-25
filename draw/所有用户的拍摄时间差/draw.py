#coding=utf-8

import os,sys
from pylab import *
mpl.rcParams['font.sans-serif'] = ['SimHei'] #指定默认字体  
mpl.rcParams['axes.unicode_minus'] = False #解决保存图像是负号'-'显示为方块的问题  

parentdir=os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(parentdir)
import numpy as np
import matplotlib.pyplot as plt
import myutil.fileutil as fu
import myutil.myconfig as mc

labels=[u'第1切割距离', u'第2切割距离', u'第3切割距离', u'第4切割距离']

matrix=fu.getCurFileMatrix(mc.figure_input_file_name, ",")
#print matrix
plt.figure(figsize=(mc.figure_width + 2, mc.figure_height + 2), dpi=mc.figure_dpi)
x = range(len(matrix[0]))
for i in range(0, len(matrix)):
	plt.bar(x, matrix[i], align="center", color='blue')

xlabels=[u'<10(m)', u'10-30(m)', u'30-60(m)', u'1-2(h)', u'2-3(h)', u'3-4(h)', u'4-8(h)', u'8-16(h)', u'16-24(h)', u'24-48(h)', u'>48(h)']
plt.xticks(x, xlabels, rotation=30)
plt.legend(loc='upper right', frameon=False)

plt.grid(True)
plt.xlabel(u'时间间隔')
plt.ylabel(u'百分比(%)')

plt.tight_layout()
mc.saveFig(plt)
plt.show()