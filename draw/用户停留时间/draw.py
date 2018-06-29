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


labels=[u'本地居民', u'外地居民', 'line3', 'line4', 'line5', 'line6']

matrix=fu.getCurFileMatrix(mc.figure_input_file_name, ",")
#print matrix
plt.figure(figsize=(5, 5), dpi=mc.figure_dpi)
x=range(0, len(matrix[0]))
for i in range(0, len(matrix)):
	plt.plot(x, matrix[i], mc.figure_line_styles[i],linewidth=1, label=labels[i])

plt.legend(loc='upper center', frameon=False)
xlabels=[u'小于1天', u'1-2天', u'2-3天', u'3-4天', u'4-5天', u'5-6天', u'6-7天', u'7-30天', u'30-365天', u'1-3年', u'大于3年']
plt.xticks(x, xlabels, rotation=20)

plt.grid(True)
plt.xlabel(u'时间间隔')
plt.ylabel(u'百分比')
plt.tight_layout()
mc.saveFig(plt)
plt.show()