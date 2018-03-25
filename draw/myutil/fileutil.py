#coding=utf-8
import sys,os
def getCurDir():
	path=sys.path[0]
	if os.path.isdir(path):
		return path
	elif os.path.isfile(path):
		return os.path.dirname(path)

def getCurPathAndName(fileName):
	curDir=getCurDir()
	pathAndName=curDir+'\\'+fileName
	return pathAndName
	
def getFileMatrix(pathAndName, splitStr):
	lines=open(pathAndName).read().split('\n')#读取所有的行
	matrix=[]
	for line in lines:
		line=line.rstrip(splitStr)
		#print line
		temp=[float(word) for word in line.split(splitStr)]
		matrix.append(temp)
	return matrix
	
def getFileDict(pathAndName, splitStr):
	dict = {}
	lines=open(pathAndName).read().split('\n')#读取所有的行
	matrix=[]
	for line in lines:
		(key, value) = line.split(splitStr)
		dict[key] = int(value)
	return dict
	
def getFileBarData(pathAndName, splitStr):
	lines=open(pathAndName).read().split('\n')
	label=lines[0].split(splitStr)#获得标注
	matrix=[]
	for index, line in enumerate(lines):
		if index>0:
			temp=[float(word) for word in line.split(splitStr)]
			matrix.append(temp)
	return (label, matrix)	

#获取相对于当前文件夹下的文件
def getCurFileMatrix(fileName, splitStr):
	pathAndName=getCurPathAndName(fileName)
	return getFileMatrix(pathAndName, splitStr)
	
def getCurFileBarData(fileName, splitStr):
	pathAndName=getCurPathAndName(fileName)
	return getFileBarData(pathAndName, splitStr)
	
def getCurFileDict(fileName, splitStr):
	pathAndName=getCurPathAndName(fileName)
	return getFileDict(pathAndName, splitStr)
	
def getFileTupleList(pathAndName, splitStr):
	lines=open(pathAndName).read().split('\n')
	tupleList=[]
	for line in lines:
		line=line.rstrip(splitStr)
	#	temp=tuple(line)字符
		temp=tuple(float(word) for word in line.split(splitStr))
		tupleList.append(temp)
	return tupleList
