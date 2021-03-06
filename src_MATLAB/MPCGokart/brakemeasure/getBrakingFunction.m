%code by mh
clear all
addpath('..')
addpath('../SystemAnalysis')
folder = strcat(getuserdir,'/Documents/ML_out/');
file = 'brakingMLData.csv';
tireradius = 0.12;
% whole table: [t x y Ksi dotx_b doty_b dotKsi  dotdotx_b dotdoty_b dotdotKsi sa sdota pcl pcr wrl wrt dotwrl dotwrr lp ltemp dotltemp]
M = csvread(strcat(folder,file));
temp = M(:,20);
dottemp = M(:,21);
acc = tireradius*mean(M(:,17:18),2);
acc = gaussfilter(acc,10);
spd = tireradius*mean(M(:,15:16),2);
bpos = -M(:,19)/100000;
brakestart = 2.75;
brakeend = 3.9;
bselect = bpos > brakestart & bpos < brakeend & spd>0.2;
bselect = imerode(bselect,ones(100,1));
nbselect = bpos < 0.6 & bpos > 0.4;
nbselect = imerode(nbselect,ones(100,1));
dottemp(1:1000)=0;
dottemp(end-1000:end)=0;
t = M(:,1);
close all
subplot(2,2,1)
title('brake position and heat')
hold on
xlabel('Time [s]')
yyaxis left
ylabel('position')
plot(t,-M(:,19))
yyaxis right
ylabel('temp °C]')
plot(t,M(:,20))
hold off

pbrake = polyfit(bpos(bselect), acc(bselect),2);
xb = brakestart:0.01:brakeend;
yb = polyval(pbrake,xb);
subplot(2,2,2)
title('effect of brake')
hold on
xlabel('Brakingposition [cm]')
ylabel('additional Acceleration [m/s²]')
scatter(bpos(bselect), acc(bselect));
plot(xb,yb);
hold off

subplot(2,2,3)
hold on
title('cooldown (no braking)')
xlabel('temp [°C]')
ylabel('temp change [°C/s]')
scatter(temp(nbselect), dottemp(nbselect));
hold off

subplot(2,2,4)
hold on
title('heatup (braking)')
xlabel('brake')
ylabel('temp change [°C/s]')
scatter(bpos(bselect), dottemp(bselect));
hold off