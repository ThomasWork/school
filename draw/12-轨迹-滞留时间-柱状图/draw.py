#coding=utf-8
import os,sys
from pylab import *
parentdir=os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(parentdir)
import myutil.fileutil as fu
import myutil.myconfig as mc

mpl.rcParams['font.sans-serif'] = ['SimHei'] #指定默认字体   
mpl.rcParams['axes.unicode_minus'] = False #解决保存图像是负号'-'显示为方块的问题  


#plt.rc('text', usetex=True)

matrix=fu.getCurFileMatrix(mc.figure_input_file_name, ",")
#print matrix
#获取数量
value=[float(word) for word in matrix[1]]
#print value
#获取百分比
percent=[float(word) for word in matrix[2]]
#print percent

fig, ax1 = plt.subplots(figsize=(mc.figure_width,mc.figure_height), dpi=mc.figure_dpi)
ax2 = ax1.twinx()

x = np.arange(len(value))
bar1 = ax1.bar(x, value, align="center", color='blue')
ax1.set_ylabel(u'用户数量', color='blue')

lin2 = ax2.plot(x, percent, 'r-*')
ax2.set_ylabel(u'累积用户数量百分比', color='red')
ax2.set_ylim(0, 1)

ax1.set_xticks(x)
ax1.set_xticklabels(matrix[0], rotation=0)

ax1.grid(True)
ax2.grid(True)
ax1.set_xlabel(u'停留时间（*24小时）')

#调整画图区域的位置

plt.subplots_adjust(left=0.16, right=0.86, bottom=0.17, top=0.96)
#plt.subplots_adjust()

mc.saveFig(plt, 'fig_choose_block.pdf')
plt.show()