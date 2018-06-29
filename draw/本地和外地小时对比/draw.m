clear;
clc;
load data.txt
x=data(1, :);
cpu=data(2, :);
gpu=data(3, :);
plot(x, cpu, '-ob', x, gpu, '-*r');
xlabel('�û���', 'fontsize', 12);
ylabel('ִ��ʱ��(ms)', 'fontsize', 12);
legend('CPU', 'GPU');