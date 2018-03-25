# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d.art3d import *

fig = plt.figure()
ax = fig.gca(projection='3d')

# 正方体顶点和面
verts = [(0, 0, 0), (0, 1, 0), (1, 1, 0), (1, 0, 0), (0, 0, 1), (0, 1, 1), (1, 1, 1), (1, 0, 1)]
faces = [[0, 1, 2, 3], [4, 5, 6, 7], [0, 1, 5, 4], [1, 2, 6, 5], [2, 3, 7, 6], [0, 3, 7, 4]]
# 四面体顶点和面
# verts = [(0, 0, 0), (1, 0, 0), (1, 1, 0), (1, 0, 1)]
# faces = [[0, 1, 2], [0, 1, 3], [0, 2, 3], [1, 2, 3]]
# 获得每个面的顶点
poly3d = [[verts[vert_id] for vert_id in face] for face in faces]
# print(poly3d)

# 绘制顶点
x, y, z = zip(*verts)
ax.scatter(x, y, z)
# 绘制多边形面
#ax.add_collection3d(Poly3DCollection(poly3d, facecolors='w', linewidths=1, alpha=0.3))

ax.add_collection3d(Line3DCollection(poly3d, colors='k', linewidths=0.5, linestyles=':'))

 # 设置图形坐标范围ax.set_xlabel('X')ax.set_xlim3d(-0.5, 1.5)ax.set_ylabel('Y')ax.set_ylim3d(-0.5, 1.5)ax.set_zlabel('Z')ax.set_zlim3d(-0.5, 1.5)plt.show()
 
plt.show()