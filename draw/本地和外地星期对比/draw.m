clear;
clc;
load data.txt
x=data(1, :);
cpu=data(2, :);
gpu=data(3, :);
plot(x, cpu, '-ob', x, gpu, '-*r');
xlabel('用户数', 'fontsize', 12);
ylabel('执行时间(ms)', 'fontsize', 12);
legend('CPU', 'GPU');