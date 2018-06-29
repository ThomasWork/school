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
from scipy.cluster.hierarchy import dendrogram, linkage
from scipy.cluster.hierarchy import fcluster
from scipy.cluster.hierarchy import cophenet
from scipy.spatial.distance import pdist
import numpy as np

def fancy_dendrogram(*args, **kwargs):
	max_d = kwargs.pop('max_d', None)
	if max_d and 'color_threshold' not in kwargs:
		kwargs['color_threshold'] = max_d
	annotate_above = kwargs.pop('annotate_above', 0)

	ddata = dendrogram(*args, **kwargs)

	if not kwargs.get('no_plot', False):
		plt.title('Hierarchical Clustering Dendrogram (truncated)')
		plt.xlabel('sample index or (cluster size)')
		plt.ylabel('distance')
		for i, d, c in zip(ddata['icoord'], ddata['dcoord'], ddata['color_list']):
			x = 0.5 * sum(i[1:3])
			y = d[1]
			if y > annotate_above:
				plt.plot(x, y, 'o', c=c)
				plt.annotate("%.3g" % y, (x, y), xytext=(0, -5), textcoords='offset points', va='top', ha='center')
		if max_d:
			plt.axhline(y=max_d, c='k')
	return ddata

matrix=fu.getCurFileMatrix(mc.figure_input_file_name, ",")
# a custom function that just computes Euclidean distance
def mydist(p1, p2):
    diff = p1 - p2
    return np.vdot(diff, diff) ** 0.5


def mydist2(p1, p2):
	x = int(p1[0])
	y = int(p2[0])
	#print p1[0], p2[0], x, y
	return matrix[x][y]
	#return pow((p1[0] + p2[0]), 2)

X = np.random.randn(833, 1)
for i in range(len(X)):
	X[i] = i + 0.1
print X

#fclust1 = fclusterdata(X, 1.0, metric=mydist2)

Z = linkage(X, 'average', metric=mydist2)
c, coph_dists = cophenet(Z, pdist(X))
print c
#print Z
#print Z[-2:,2]
plt.figure(figsize=(8, 6))
#plt.title(u'外地游客访问路径层次聚类结果')
plt.xlabel(u'路径下标')
plt.ylabel(u'类间距离')
#dendrogram(Z, leaf_rotation=90., leaf_font_size=8.)
dendrogram(Z, truncate_mode='lastp', p=300, leaf_rotation=0., leaf_font_size=12., show_contracted=True)
max_d = 0.4
#plt.axhline(y=max_d, c='r')
#fancy_dendrogram(Z, truncate_mode='lastp', p=20, leaf_rotation=90., leaf_font_size=12., show_contracted=True, annotate_above=10, max_d=0.3)
plt.tight_layout()
plt.show()
mc.saveFig(plt)
clusters = fcluster(Z, max_d, criterion='distance')
print clusters
curDir=fu.getCurDir()
np.savetxt(curDir+"/clusters.txt", clusters, fmt="%d", delimiter=",");


#参考网址：https://haojunsui.github.io/2016/07/16/scipy-hac/