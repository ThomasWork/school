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

matrix=fu.getCurFileStrMatrix(mc.figure_input_file_name, ",")
#print matrix
#获取数量
value=[float(word) for word in matrix[1]]
print value

plt.figure(figsize=(mc.figure_width+6,mc.figure_height), dpi=mc.figure_dpi)

x = np.arange(len(matrix[0]))
plt.bar(x, value, align="center")

plt.xticks(x, matrix[0])#, rotation='vertical')

plt.grid(True)
plt.xlabel(u'时间段')#注意，如果不能使用中文，可以复制一个可以使用中文的文档，然后把代码重新粘贴进去
plt.ylabel(u'数量')
#plt.title('Number of models processed per block')

mc.saveFig(plt, 'fig_choose_block.pdf')
plt.show()