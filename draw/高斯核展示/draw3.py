import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# Create Map
cm = plt.get_cmap("RdYlGn")

x = np.random.rand(30)
y = np.random.rand(30)
z = np.random.rand(30)

col = np.arange(30)

fig = plt.figure()
ax3D = fig.add_subplot(111, projection='3d')
ax3D.scatter(x, y, z, s=30, c=col, marker='o', cmap=cm)

plt.axhline(y=0.87)

plt.show()