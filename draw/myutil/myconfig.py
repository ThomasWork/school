#coding=utf-8

import fileutil as fu

figure_width=4
figure_height=3
figure_dpi=100
figure_save_name='/my.png'
figure_show_dip=100
figure_save_dpi=100
figure_input_file_name='data.txt'
figure_line_styles=['-o', '-*', '-<', '-+', '-H']
figure_use_path='C:/Users/Admin/Desktop/Paper/imgs/'
def saveFig(plt, dpiP=figure_save_dpi):
	imgName=fu.getCurDir() + figure_save_name
	plt.savefig(imgName, dpi=dpiP)
	
def savePDF(plt, name='paper.pdf', pathAndName=fu.getCurDir() + figure_save_name):
	plt.savefig(pathAndName, dpi=figure_save_dpi)#保存到当前文件夹
	#plt.savefig(figure_use_path+name, dpi=figure_save_dpi)
	