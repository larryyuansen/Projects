# """
# Comments:
# Notebook for used code in Stage 1:
# Part A:
#     aPart: initial as 0
#         0  : Error: not revieve Reply, should jump to Part F.
        
#         1  : Recieved ICMP reply.

# Part B: For ICMP
#     bPart: initial as -2 
#         -3 : Error: ICMP reply answers smaller than 3, cannot determine
#         -2 : Error: part A no anser, part B skiped
        
#         -1 : IP/ID counter type is: random.
#         0  : IP/ID counter type is: zero.
#         >0 : IP/ID counter type is: incremental.

# Part C:
#     cPart: initial as 0 
#         0  : Error: part A no answer or TCP not resonse, part C skiped
        
#         1  : Port 80 is open

# Part D: For TCP
#     dPart: initial as -2 
#         -3 : Error: TCP reply answers smaller than 3, cannot determine
#         -2 : Error: part A or C no answer, part D skiped
        
#         -1 : IP/ID counter type is: random.
#         0  : IP/ID counter type is: zero.
#         >0 : IP/ID counter type is: incremental.

# Part E:
#     ePart: initial as 0 
#         -1 : Error: no response on request
#         0  : Error: part A or C no response
        
#         1  : SYN cookies likely not deployed
#         2  : SYN cookies likely deployed

# Part F:
#     fPart: initial as 0
#         -1 : Error: cannot determine
#         0  : Error: device is not responsive (part A no response)
        
#         >0 : list combination below:
#             1  : Linux 2.4 and 2.6, Google customized Linux, Linux kernel 2.2, FreeBSD, OpenBSD, AIX 4.3 or MAC
#             2  : Windows 2000, XP, 7, Vista, and Server 8
#             3  : Sisco Router IOS 12.4, Solaris 7
#             4  : Google customized Linux
#             5  : Linux 2.4 and 2.6
#             6  : OpenBSD, AIX 4.3
#             7  : Linux kernel 2.2
#             8  : FreeBSD, MAC
#             9  : Windows 7, Vista and Server 8
#             10 : Windows 2000
#             11 : Windows XP
#             12 : Sisco Router IOS 12.4
#             13 : Solaris 7
# """

# import scapy
from scapy.all import *
import sys

# For cheking ID-IP counter type
def checkType(ans):
    idc = 0
    for i in range (len(ans)-1):
        if idc==0:
            idc=ans[i+1][1].id - ans[i][1].id
        else:
            if idc==(ans[i+1][1].id - ans[i][1].id):
                continue
#             print(ans[i+1][1].id - ans[i][1].id)
            else:
                idc=-1
    return idc

# help method, for debug only, uesless for main code
def counterType(idc):
    if idc == 0:
        print("IP/ID counter type is: zero.")
    elif idc == -1:
        print("IP/ID counter type is: random.")
    elif idc > 0:   
        print("IP/ID counter type is: incremental.")
    else:
        print("Error")
    return

# For guesting likly OS of remote machine
def CheckOS(ttlR,winR):
    os=-1
    ttl = ttlR.ttl
    if winR is None:   
        if ttl<=64:
            os = 1
        elif ttl<=128:
            os = 2
        elif ttl<=255:
            os = 3
        else:
            os = -1
    else:
        windowSize = winR.window
        if ttl<=64:
            if windowSize<=5720:
                os=4
            elif windowSize<=5840:
                os=5
            elif windowSize<=16384:
                os=6
            elif windowSize<=32120:
                os=7
            elif windowSize<=65535:
                os=8
            else:
                os=-1 
        elif ttl<=128:
            if windowSize<=8192:
                os=9
            elif windowSize<=16384:
                os=10
            elif windowSize<=65535:
                os=11
            else:
                os=-1 
        elif ttl<=255:
            if windowSize<=4182:
                os=12
            elif windowSize<=8763:
                os=13
            else:
                os=-1 
        else:
            os = -1

    return os

# Stage 1 process
def stage1(ipAddress):
    ip = ipAddress
    print("")
    print(ip,":")
    ICMPr1 = None 
    ICMPr = None 
    ICMPur = None 
    TCPr1 = None 
    TCPr = None 
    TCPur = None 
    eTCPr = None 
    eTCPur = None 
#     Part A: Device is responsive:
    aPart = 0
    print("Part A:")
    ICMPr1 = sr1(IP(dst=ip)/ICMP(), timeout=5)
    if ICMPr1 is not None:
        aPart = 1 # part a got answer
#     else:
#         print("There is no response and request timeout for ICMP request")

#     Part B: IP-ID counter deployed by device (in ICMP pkts):
    bPart = -2
    if aPart==1:# if part a got answer
        print("Part B:")
        r = []
        rr = []   
        for i in range(5):
            r1 = sr1(IP(dst=ip)/ICMP(), timeout=5)
            if r1 is not None:
                rr.append((r,r1))
        if (len(rr)>2):
            bPart = checkType(rr)
        else:
            ICMPr,ICMPur = srloop(IP(dst=ip)/ICMP(),count=5)
            if len(ICMPr)>2:
                bPart = checkType(ICMPr)
            else:
                bPart = -3
#             print("Recieve packets less than 3, cannot determin the IP-ID couter type.")

#     Part C: Port 80 on device is open:
    cPart = 0
    if aPart==1:# if part a got answer
        print("Part C:")
        TCPr1 = sr1(IP(dst=ip)/TCP(flags='S',dport=80), timeout=5)
        if TCPr1 is not None:
            cPart = 1
#         else:
#             print("There is no response TCP ACK and request timeout for TCP SYN request")

#     Part D: IP-ID counter deployed by device (in TCP pkts):
    dPart = -2
    if (aPart==1 and cPart==1):# if part a, c got answer
        print("Part D:")
        r = []
        rr = []   
        for i in range(5):
            r1 = sr1(IP(dst=ip)/TCP(flags='S',dport=80), timeout=5)
            if r1 is not None:
                rr.append((r,r1))
        if (len(rr)>2):
            dPart = checkType(rr)
        else:
            TCPr,TCPur = srloop(IP(dst=ip)/TCP(flags='S',dport=80),count=5)
            if len(TCPr)>2:
                dPart = checkType(TCPr)
            else:
                dPart = -3
#             print("Recieve packets less than 3, cannot determin the IP-ID couter type.")

    # Part E: SYN cookies deployed by device:
    ePart = 0
    if (aPart==1 and cPart==1):# if part a, c got answer
        print("Part E:")
        # eTCPr,eTCPur = sr(IP(dst=ip)/TCP(flags='S',dport=80), timeout=0.5, multi =1)
        eTCPr,eTCPur = sr(IP(dst=ip)/TCP(flags='S',dport=80), timeout=60, multi =1)
        if eTCPr is None:
            ePart = -1
        elif len(eTCPr)==0:
            ePart = -1
        elif len(eTCPr)>1:
            ePart = 1
        elif len(eTCPr)==1:
            ePart = 2

    # Part F: Likely OS system deployed on the device:
    fPart = 0
    if aPart==1:
        print("Part F:")
        if cPart==1:
            fPart=CheckOS(ICMPr1,TCPr1)
            print()
        else:
            fPart=CheckOS(ICMPr1,None)

    # return aPart, bPart, cPart, dPart, ePart, fPart, ICMPr1, ICMPr, ICMPur, TCPr1, TCPr, TCPur, eTCPr
    return aPart, bPart, cPart, dPart, ePart, fPart

def evaluationPrint(ip,a,b,c,d,e,f):
    print("Evaluation output:")
    print(ip,":")
    print(" Part A:")
    if a==0:
        print("  Device is not responsive, reconnaissance terminated.")
    else:
        print("  Device is responsive.")
        print(" Part B:")
#         Part B
        if b==-3:
            print("  Error: ICMP reply answers smaller than 3")
        else:
            print("  IP-ID counter deployed by device (in ICMP pkts) is",end=' ')
            if b==-1:
                print("random.")
            elif b==0:
                print("zero.")
            elif b>0:
                print("incremental.")
        
        print(" Part C:")
        if c==0:
            print("  Port 80 on device is not open.")
        else:
            print("  Port 80 on device is open.")
            print(" Part D:")
            if d==-3:
                print("  Error: TCP reply answers smaller than 3.")
            else:
                print("  IP-ID counter deployed by device (in TCP pkts) is", end=' ')
                if d==-1:
                    print("random.")
                elif d==0:
                    print("zero.")
                elif d>0:
                    print("incremental.")
                    
            
            print(" Part E:")
            if e==-1:
                print("  Error: no response on request.")
            else:
                if e==1:
                    print("  SYN cookies not deployed.")
                elif e==2:
                    print("  SYN cookies likely deployed.")
        
        print(" Part F:")
        if f==-1:
            print("   Error: cannot determine.")
        else:
            print("  Likely OS system deployed on the device is: ", end=' ')
            if f==1:
                print("Linux 2.4 and 2.6, Google customized Linux, Linux kernel 2.2, FreeBSD, OpenBSD, AIX 4.3 or MAC.")
            elif f==2:
                print("Windows 2000, XP, 7, Vista, and Server 8.")
            elif f==3:
                print("Sisco Router IOS 12.4, Solaris 7.")
            elif f==4:
                print("Google customized Linux.")
            elif f==5:
                print("Linux 2.4 and 2.6.")
            elif f==6:
                print("OpenBSD, AIX 4.3.")
            elif f==7:
                print("Linux kernel 2.2.")
            elif f==8:
                print("FreeBSD or MAC.")
            elif f==9:
                print("Windows 7, Vista and Server 8.")
            elif f==10:
                print("Windows 2000.")
            elif f==11:
                print("Windows XP.")
            elif f==12:
                print("Sisco Router IOS 12.4.")
            elif f==13:
                print("Solaris 7.")
            else:
                print("Not Determin.")

# Main Process
ip = input("Enter IP to scan: ")
# ip = "106.52.115.145"
a,b,c,d,e,f = stage1(ip)
evaluationPrint(ip,a,b,c,d,e,f)

# # Stage 2 Code:
# # Stage 2:
# # read from input file
# fileName = "Sony.txt"
# file1 = open(fileName,"r+")
# sonyIPs = file1.read().splitlines()
# file1.close()

# # Remove Duplicate IP address
# sonyIPs = list(dict.fromkeys(sonyIPs))

# sonyIPs.remove("IP")

# print(len(sonyIPs))
# # sonyIPs.sort()
# # sonyIPs

# aa=[] 
# bb=[]
# cc=[]
# dd=[]
# ee=[]
# ff=[]
# count = 0 
# for ips in sonyIPs:
#     count+=1
#     print("Percent: ",count, " / ", len(sonyIPs))
#     # a1,b1,c1,d1,e1,f1,i11,i21,i31,t11,t21,t31,et1 = stage1(ips)
#     a1,b1,c1,d1,e1,f1= stage1(ips)
#     aa.append(a1)
#     bb.append(b1)
#     cc.append(c1)
#     dd.append(d1)
#     ee.append(e1)
#     ff.append(f1)  

# print("1. Name of IoT selected: Sony_IP_Camera")
# print("2. Responsive devices:", aa.count(1), ",",(aa.count(1)/len(sonyIPs))*100,"%")
# print("3.IP-ID in ICMP pkts (given devices with response):")
# print(" Random:",bb.count(-1),",",(bb.count(-1)/aa.count(1))*100,"%,",end='')
# print(" Zero:",bb.count(0),",",(bb.count(0)/aa.count(1))*100,"%,",end='')
# print(" Incremental:",bb.count(1),",",(bb.count(1)/aa.count(1))*100,"%.")
# print("4. Port 80 open:", cc.count(1), ",",(cc.count(1)/len(sonyIPs))*100,"%")
# print("5. IP-ID in TCP pkts (given devices with port 80 open):")
# print(" Random:",dd.count(-1),",",(dd.count(-1)/cc.count(1))*100,"%,",end='')
# print(" Zero:",dd.count(0),",",(dd.count(0)/cc.count(1))*100,"%,",end='')
# print(" Incremental:",dd.count(1),",",(dd.count(1)/cc.count(1))*100,"%.")
# print("6. Devices that deploy SYN coockie(given devices with port 80 open): ",end='' )
# print(ee.count(1), ",",(ee.count(1)/cc.count(1))*100,"%.")
# windows=ff.count(1)+ff.count(5)+ff.count(7)
# linux=ff.count(2)+ff.count(9)+ff.count(10)+ff.count(11)
# print("7. Devices operation system(given devices with response):")
# print(" Linux:",linux,",",linux/aa.count(1),"%,",end='')
# print(" Windows:",windows,",",windows/aa.count(1)*100,"%.")     