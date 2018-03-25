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


labels,matrix=fu.getCurFileBarData(mc.figure_input_file_name, ",")
labels=[u'小于1天', u'1-3天', u'3天至1星期', u'1星期至1个月', u'1个月至1年', u'1年至3年', u'大于3年']
for index,label in enumerate(labels):
	labels[index]=label
length=len(matrix[0])
x = np.arange(0, len(matrix[0]), 1)
y1 = matrix[0]
y2 = matrix[1]

fig, ax1 = plt.subplots()
ax2 = ax1.twinx()

axs=[ax1, ax2]

colors=['g', 'b']

for i in range(0, len(matrix)):
	axs[i].plot(x, matrix[i], mc.figure_line_styles[i],linewidth=1, color=colors[i])
	
ax1.set_xticklabels(labels) 
ax1.set_xlabel(u'时间间隔')
ax1.grid(True)
ax1.set_ylabel(u'非北京用户', color=colors[0])
ax2.set_ylabel(u'北京用户', color=colors[1])

mc.saveFig(plt)
plt.show()