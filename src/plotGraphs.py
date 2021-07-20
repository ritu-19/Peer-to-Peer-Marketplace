
import matplotlib.pyplot as plt
import matplotlib.pyplot as plt; plt.rcdefaults()
import numpy as np
import os

def plot_bar_x(data, label, text):
    index = np.arange(len(data))
    plt.bar(index, data, align='center', alpha=0.5)
    plt.xticks(index, label)
    plt.ylabel('Average Time')
    plt.title(text)

    oldpwd=os.getcwd()
    dirName = "../Perf-Metrics/"
    # Create target Directory if don't exist
    if not os.path.exists(dirName):
        os.mkdir(dirName)
    os.chdir(dirName)
    #plt.show()
    plt.savefig(text+'jpg')
    plt.show()
    os.chdir(oldpwd)

def main():
    n = input("Enter the number of peers ")
    print(n)

    dirName = "PerfMetricsLogs/"
    os.chdir(dirName)

    file1 ="announce-"
    file2 = "getProduct-"
    file3 = "buyProd-"
    averageAnnounce = []
    averageGetProd = []
    averageBuyProd = []

    label = []
    lines1 = []
    for i in range(int(n)):
        label.append(i+1)

    for i in range(int(n)):
        lines1 = []
        with open(str(file1) + str(i+1)) as f1:
            lines1 = [int(line.rstrip()) for line in f1]

        if len(lines1) > 0:
            averageAnnounce.append((sum(lines1)/len(lines1)) * 0.001)
        else:
            averageAnnounce.append(0)

        f1.close()

    for data in averageAnnounce:
        print(data)

    text = "Average Announce Time (micro seconds)"
    plot_bar_x(averageAnnounce, label, text)
    lines2 = []
    for i in range(int(n)):
        lines2 = []
        with open(str(file2) + str(i+1)) as f2:
            lines2 = [int(line.rstrip()) for line in f2]

        if len(lines2) > 0:
            averageGetProd.append((sum(lines2)/len(lines2)) * 0.001)
        else:
            averageGetProd.append(0)
        f2.close()

    for data in averageGetProd:
        print(data)

    text = "Average Get Product Time(micro seconds)"
    plot_bar_x(averageGetProd, label, text)


    for i in range(int(n)):
        lines3 = []
        with open(str(file3) + str(i+1)) as f3:
            #lines3 = [int(line.rstrip()) for line in f3]
            for line in f3:
                #print(line.rstrip())
                lines3.append(int(line))

        if len(lines3) > 0:
            averageBuyProd.append((sum(lines3)/len(lines3)) * 0.001)
        else:
            averageBuyProd.append(0)
        f3.close()

    for data in averageBuyProd:
        print(data)

    text = "Average Buy Product Time(micro seconds)"
    plot_bar_x(averageBuyProd, label, text)

if __name__ == "__main__":
    main()