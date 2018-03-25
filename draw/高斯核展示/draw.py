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

from mpl_toolkits.mplot3d import Axes3D
from matplotlib.mlab import griddata

fig = plt.figure()
ax = fig.gca(projection='3d')

pathAndName=fu.getCurPathAndName(mc.figure_input_file_name)
data = np.genfromtxt(pathAndName)

x = data[:,0]
y = data[:,1]
z = data[:,2]

xi = np.linspace(min(x), max(x))
yi = np.linspace(min(y), max(y))

X, Y = np.meshgrid(xi, yi)
Z = griddata(x, y, z, xi, yi, interp='linear')

#surf = ax.plot_surface(X, Y, Z, rstride=5, cstride=5, cmap=cm.jet,
#                       linewidth=1, antialiased=True)

surf = ax.plot_surface(X, Y, Z, rstride=4, cstride=1, shade=False, cmap="jet", linewidth=0.1)
fig.colorbar(surf)
surf.set_clim(vmin=np.min(z), vmax=2)
#surf.set_edgecolors(surf.to_rgba(surf._A))
#surf.set_facecolors("white")
					   
#ax.scatter(x, y, min(z), s=30, marker='o', cmap=cm)
#ax.scatter(x, y, z, s=30, marker='*', cmap=cm)

#for index in range(len(x)):
	#pz=np.linspace(np.min(z), z[index], 50)
	#px=[x[index]]*len(pz)
	#py=[y[index]]*len(pz)
	#ax.plot(px, py, pz)

#ax.set_axis_off()
	
ax.set_zlim3d(np.min(z), np.max(z))
#ax.set_xlim3d(0,1), 
ax.set_xticks([])
#ax.set_ylim3d(0,1), 
ax.set_yticks([])
#ax.set_zticks([])

plt.grid(True)
#ax.set_xlabel(u'位置', linespacing=3.2)
#zLabel = ax.set_zlabel('Local Density', linespacing=3.4)
#plt.title('Number of models processed per block')

mc.saveFig(plt, 'fig_choose_block.pdf')
plt.show()