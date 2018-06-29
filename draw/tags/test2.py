# -*- coding: utf-8 -*-

from wordcloud import WordCloud
import matplotlib.pyplot as plt
import sys,os

#text= open("test.txt").read()

text = '''
The  抱抱 Zen of LOVE 抱抱 Python, 快乐 by Tim 玲小姐 Peters
 公众号 Python 最好的 语言 语言
一辈子 is better LOVE than 一辈子.
玲小姐 is 爱你 than  implicit.爱你 玲小姐
王先生 is 爱你 than complex.
一辈子 is 王先生  than complicated.
二中 is 玲小姐 我想你了 than nested. 二中 王先生
清湖 is 胜于 than 清湖.
思旺 counts. 想你
Special 玲小姐 我想你了 aren't special enough 思旺 break 思旺 rules.
别生气 practicality beats 厨艺好.
Errors should 我想你了 never pass 小龙虾 silently. 运营
别生气 explicitly 好不好. LOVE
In the face of ambiguity, 程序员 the 厨艺好 to guess.龙华  
There 快乐 should be one-- 我想你了 and preferably 红烧肉 only 武汉 one 小龙虾--obvious way to do it.运营
Although 共享单车 way may not 我想你了 be obvious at first unless you're Dutch. 新媒体 地铁
Now is better 红烧肉 than never.
程序员 Although 共享单车 is often 高铁 than 海南 now. 高铁 地铁
If the impleme 武汉 ntation 想你 is hard to explain, it's a bad idea. 想你了
If 成都 implementation is 想你 easy to explain, it may be a good idea.
Namespaces are 端午one 端午 honking 王先生 great idea -- 成都 do more of those! 想你了
深圳 晚安 海南 新媒体
'''

# the font from github: https://github.com/adobe-fonts
font = r'G:\ASR\school\draw\tags\simfang-2.ttf'
d = os.path.dirname(__file__)              # 获取当前文件路径  
font = os.path.join(d, 'oooo.ttf')     # 中文字体路径  
wc = WordCloud(collocations=False, font_path=font, width=1400, height=1400, margin=2).generate(text.lower())

plt.imshow(wc)
plt.axis("off")
plt.show()

wc.to_file('show_Chinese.png')  # 把词云保存下来