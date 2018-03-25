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
	plt.plot(x, matrix[i], mc.figure_line_styles[i],linewidth=1, label=labels[i])

xlabels=[u'<8(h)', u'8-16(h)', u'16-24(h)', u'24-48(h)', u'48-72(h)', u'3-7(d)', u'7-30(d)', u'30-365(d)', u'>365(d)']
plt.xticks(x, xlabels, rotation=20)
plt.legend(loc='upper right', frameon=False)

plt.grid(True)
plt.xlabel(u'时间间隔')
plt.ylabel(u'百分比(%)')
plt.tight_layout()
mc.saveFig(plt)
plt.show()