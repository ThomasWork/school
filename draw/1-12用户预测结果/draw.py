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



#获得数据并设置行数和列数
matrix = fu.getCurFileMatrix(mc.figure_input_file_name, ",")
#print matrix
total_num = len(matrix)
column_num = 2#几列
compare_num = 2#一个图中几个比较
compare_show = 1
row_num = total_num / (column_num * compare_num)

#设置总体标题
fig = plt.figure(num=None, figsize=(6, 9), dpi = mc.figure_show_dip, facecolor='w', edgecolor='k')


#st=fig.suptitle("12 users' load curve in 80 days", fontsize="x-large")#这里可以直接设置字号
#st.set_y(0.95)
#fig.text(0.5, 0.95, 'testfffffffffffffffffffff', transform=fig.transFigure, horizontalalignment='center')
#fig.text(0.5,0.975,'The new formatter',horizontalalignment='center',verticalalignment='top')

#labels=['photos', 'users']

#画每一个子图
for i in range(0, row_num):
	for j in range(0, column_num):
		img_index=i * column_num + j
		#if img_index==10:
		#	continue
		subp=plt.subplot(row_num, column_num, img_index+1)
		for k in range(0, compare_show):
			line_index=img_index*compare_num+k
			x = [month + 1 for month in range(12)]
			subp.plot(x, matrix[line_index], mc.figure_line_styles[k + 2], linewidth=1)
		subp.grid(True);
		subp.set_xlabel(str(img_index+2008) + u'年')
		subp.set_ylabel(u'照片数')
		indStr = [str(month) + u'' for month in x]
		subp.set_xticks(x)
		subp.set_xticklabels(indStr)#使用这一句之前需要先设置刻度
		#if img_index==(10-1):
		#	subp.legend(loc='center left', bbox_to_anchor=(1.5, 0.5))

#plt.legend(title="sample")
		
#调整子图位置
fig.tight_layout()
#fig.subplots_adjust(top=0.9)

#保存图片
#mc.saveFig(plt, 'fig_12_user_compare.pdf')
mc.saveSameFolder(plt)

#显示图片
plt.show()
