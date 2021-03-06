function [powerleft, powerright, ipowerleft, ipowerright, powerviolated,diffviolated] = backtorques(beta,rreal,v,power)
    c = 1;
    kc = 1;
    ks = 0.01;
    rviolationthreshold = 0.1;
    rviolationcorrection = 0.5;

    rwanted = tan(beta)*v*c;
    
    wdiff = (rwanted-rreal)*kc + tan(beta)*v^2*ks;
    
    powerleft = power-wdiff;
    powerright = power+wdiff;
    ipowerleft = powerleft;
    ipowerright = powerright;
    
    if(powerright > 1)
       overpower = powerright-1;
       powerright = 1;
       powerleft = powerleft + overpower;
       wdiffviolation = 2*overpower;
       powerleft = powerleft-rviolationcorrection*max(wdiffviolation-rviolationthreshold,0);
    end
    if(powerleft > 1)
       overpower = powerleft-1;
       powerleft = 1;
       powerright = powerright + overpower;
       wdiffviolation = 2*overpower;
       powerright = powerright-rviolationcorrection*max(wdiffviolation-rviolationthreshold,0);
    end
    
    powerviolated = (powerleft+powerright)/2-power;
    diffviolated = (-powerleft+powerright)/2-wdiff
end

