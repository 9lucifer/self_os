package other;

import java.util.*;

class SegmentPageMemoryManagerFIFO {
    /**
     * 内存管理模拟类的私有字段
     * 这些字段共同描述了内存系统的特性、配置以及当前的状态
     */
    private int memorySize; // 内存总大小，以字节为单位
    private int blockSize; // 块大小，以字节为单位，内存被划分为多个等大小的块
    private int numBlocks; // 块的数量，根据memorySize和blockSize计算得出
    private int numProcesses; // 进程的数量，表示系统中可以同时运行的进程数
    private List<List<Integer>> processSegments; // 进程段列表，每个进程拥有一个段列表，每个段由起始地址和大小组成
    private Map<Integer, List<Integer>> accessPatterns; // 访问模式映射，键为进程ID，值为该进程的内存访问模式
    private int[] memory; // 内存数组，模拟实际的内存空间，每个元素代表一个块的状态或使用情况
    private Map<Integer, List<Integer>> pageTable; // 页表，键为页号，值为该页所在的内存块号，用于地址转换
    private Map<Integer, List<Integer>> pageAccessHistory; // 页访问历史映射，记录每个页的访问历史，用于某些内存管理算法
    private Queue<Integer> fifoQueue; // FIFO队列，用于实现先进先出的内存替换算法



    /**
     * 构造函数用于初始化内存管理器
     *
     * @param memorySize 内存的总大小
     * @param blockSize 每个内存块的大小
     * @param numBlocks 内存块的数量
     * @param numProcesses 进程的数量
     * @param processSegments 每个进程的段列表
     * @param accessPatterns 每个进程的访问模式
     */
    public SegmentPageMemoryManagerFIFO(int memorySize, int blockSize, int numBlocks, int numProcesses,
                                        List<List<Integer>> processSegments, Map<Integer, List<Integer>> accessPatterns) {
        this.memorySize = memorySize;
        this.blockSize = blockSize;
        this.numBlocks = numBlocks;
        this.numProcesses = numProcesses;
        this.processSegments = processSegments;
        this.accessPatterns = accessPatterns;
        this.memory = new int[numBlocks];
        Arrays.fill(memory, -1); // 初始化内存块为空
        this.pageTable = new HashMap<>();
        for (int i = 0; i < numProcesses; i++) {
            pageTable.put(i, new ArrayList<Integer>());
        }
        this.pageAccessHistory = new HashMap<>();
        for (int i = 0; i < numProcesses; i++) {
            pageAccessHistory.put(i, new ArrayList<Integer>());
        }
        this.fifoQueue = new LinkedList<>();
    }


    // 自定义一个 repeat 方法，替代 String.repeat(int)
    // 打印信息
    public static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }



    /**
     * 分配内存给指定进程
     *
     * @param processId 需要分配内存的进程ID
     * @param requiredBlocks 进程所需的内存块数量
     * @return 返回一个包含已分配内存块编号的列表如果无法满足进程所需的内存块数量，则返回实际能分配的内存块列表
     */
    public List<Integer> allocateMemory(int processId, int requiredBlocks) {
        List<Integer> allocatedBlocks = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            if (memory[i] == -1) { // 找到空闲内存块
                memory[i] = processId;
                allocatedBlocks.add(i);
                if (allocatedBlocks.size() == requiredBlocks) {
                    break;
                }
            }
        }
        return allocatedBlocks;
    }

    /**
     * 回收指定进程在内存中的所有块
     * 当一个进程终止或者不再需要时，其占用的内存块需要被回收，以便后续分配给其他进程
     * 此方法遍历整个内存，寻找并回收属于指定进程的所有内存块
     *
     * @param processId 进程ID，标识需要回收内存的进程
     */
    public void freeMemory(int processId) {
        // 遍历内存中的所有块
        for (int i = 0; i < numBlocks; i++) {
            // 如果当前内存块属于指定的进程，则回收该内存块
            if (memory[i] == processId) {
                memory[i] = -1; // 将内存块标记为可用
            }
        }
    }

    // FIFO置换算法：淘汰最先进入内存的页面
    public void fifoPageReplacement() {
        // 如果队列为空，则无需进行置换操作，直接返回
        if (fifoQueue.isEmpty()) {
            return;
        }
        // 从FIFO队列中取出最先进入的页面，作为将要淘汰的页面
        int pageToEvict = fifoQueue.poll();
        // 获取将要被淘汰的页面中的进程ID
        int evictedProcessId = memory[pageToEvict];
        // 打印出FIFO置换操作的信息
        System.out.println("\n" + repeatString("-", 40) + "\n【FIFO 置换】 页面 " + evictedProcessId + " 被替换\n" + repeatString("-", 40));
        // 置换该页面，将其标记为空闲（-1）
        memory[pageToEvict] = -1;
        // 显示当前内存状态
        displayMemory();
    }


    // 处理请求
    public void handleRequest() {
        Scanner scanner = new Scanner(System.in);
        // 显示欢迎信息和操作选项
        System.out.println("\n" + repeatString("=", 40));
        System.out.println("欢迎使用内存管理系统");
        System.out.println(repeatString("=",40));
        System.out.println("操作选项：");
        System.out.println("1. 分配内存");
        System.out.println("2. 回收内存");
        System.out.println("3. 查看内存使用情况");
        System.out.println("4. 退出");
        System.out.println(repeatString("=",40));

        // 主循环，处理用户输入
        while (true) {
            System.out.print("请输入操作选项 (1/2/3/4): ");
            String choice = scanner.nextLine();
            System.out.println(repeatString("-",40));
            switch (choice) {
                case "1": // 分配内存
                    System.out.print("请输入进程编号: ");
                    int processId = Integer.parseInt(scanner.nextLine());
                    System.out.print("请输入请求的内存块数: ");
                    int requiredBlocks = Integer.parseInt(scanner.nextLine());
                    // 检查进程编号有效性
                    if (processId < 0 || processId >= numProcesses) {
                        System.out.println("进程编号无效!");
                        continue;
                    }
                    // 尝试分配内存
                    if (canAllocate(requiredBlocks)) {
                        List<Integer> allocatedBlocks = allocateMemory(processId, requiredBlocks);
                        System.out.println("为进程 " + processId + " 分配内存块: " + allocatedBlocks);
                        displayMemory();
                        // 将分配的页面添加到FIFO队列中
                        fifoQueue.addAll(allocatedBlocks);
                    } else {
                        // 内存不足时，使用FIFO置换算法释放空间
                        System.out.println("内存不足，使用FIFO置换释放空间...");
                        while (!canAllocate(requiredBlocks)) {
                            fifoPageReplacement();
                        }
                        List<Integer> allocatedBlocks = allocateMemory(processId, requiredBlocks);
                        System.out.println("为进程 " + processId + " 分配内存块: " + allocatedBlocks);
                        displayMemory();
                    }
                    break;
                case "2": // 回收内存
                    System.out.print("请输入进程编号: ");
                    processId = Integer.parseInt(scanner.nextLine());
                    // 检查进程编号有效性
                    if (processId < 0 || processId >= numProcesses) {
                        System.out.println("进程编号无效!");
                        continue;
                    }
                    freeMemory(processId);
                    System.out.println("已回收进程 " + processId + " 的内存!");
                    displayMemory();
                    break;
                case "3": // 查看内存使用情况
                    displayMemory();
                    break;
                case "4": // 退出
                    // 结束程序
                    System.out.println("\n" + repeatString("=",40));
                    System.out.println("感谢使用内存管理系统!");
                    System.out.println(repeatString("-",40));
                    scanner.close();
                    return;
                default:
                    System.out.println("无效选项，请重新选择!");
            }
        }
    }


    /**
     * 检查是否有足够的空闲内存块来分配
     *
     * 此方法通过遍历内存数组来统计空闲内存块的数量如果空闲内存块的数量
     * 达到或超过了所需分配的块数，则返回true，表示可以进行分配；否则返回false
     *
     * @param requiredBlocks 所需分配的内存块数量
     * @return 如果有足够的空闲内存块，则返回true；否则返回false
     */
    public boolean canAllocate(int requiredBlocks) {
        // 初始化空闲内存块的计数器
        int freeBlocks = 0;
        // 遍历内存数组，统计空闲内存块的数量
        for (int i : memory) {
            // 当内存块未被使用时，计数器增加
            if (i == -1) {
                freeBlocks++;
            }
        }
        // 判断是否有足够的空闲内存块来满足分配需求
        return freeBlocks >= requiredBlocks;
    }


    // 显示当前内存使用情况
    public void displayMemory() {
        // 打印分割线，用于区分不同部分的信息
        System.out.println("\n" + repeatString("=", 40));
        // 打印内存使用情况的标题
        System.out.println("当前内存使用情况：");
        // 打印副标题的分割线
        System.out.println(repeatString("-",40));
        // 遍历内存块，打印每个内存块的使用情况
        for (int i = 0; i < numBlocks; i++) {
            // 根据内存块的状态，设定其显示状态为"空闲"或"进程 x"
            String status = (memory[i] == -1) ? "空闲" : "进程 " + memory[i];
            // 格式化输出内存块编号和状态
            System.out.printf("内存块 %2d: %s\n", i, status);
        }
        // 打印结束的分割线
        System.out.println(repeatString("=",40));
    }


    // 主方法
    public static void main(String[] args) {
        // 创建Scanner对象以读取用户输入
        Scanner scanner = new Scanner(System.in);

        // 提示用户输入内存的总大小，并读取输入
        System.out.print("请输入内存的总大小 (KB): ");
        int memorySize = Integer.parseInt(scanner.nextLine());

        // 提示用户输入内存块的大小，并读取输入
        System.out.print("请输入内存块的大小 (KB): ");
        int blockSize = Integer.parseInt(scanner.nextLine());

        // 计算内存块的数量
        int numBlocks = memorySize / blockSize;

        // 提示用户输入进程数，并读取输入
        System.out.print("请输入进程数: ");
        int numProcesses = Integer.parseInt(scanner.nextLine());

        // 输入每个进程的段数和页数
        List<List<Integer>> processSegments = new ArrayList<>();
        for (int i = 0; i < numProcesses; i++) {
            // 提示用户输入当前进程的段数，并读取输入
            System.out.print("请输入进程 " + i + " 的段数: ");
            int numSegments = Integer.parseInt(scanner.nextLine());

            List<Integer> segments = new ArrayList<>();
            for (int j = 0; j < numSegments; j++) {
                // 提示用户输入当前进程的当前段的页数，并读取输入
                System.out.print("请输入进程 " + i + " 的第 " + (j + 1) + " 段的页数: ");
                int numPages = Integer.parseInt(scanner.nextLine());
                segments.add(numPages);
            }
            processSegments.add(segments);
        }

        // 输入每个进程的页面访问顺序
        Map<Integer, List<Integer>> accessPatterns = new HashMap<>();
        for (int i = 0; i < numProcesses; i++) {
            // 提示用户输入当前进程的页面访问顺序，并读取输入
            System.out.print("请输入进程 " + i + " 的页面访问顺序，用空格分隔: ");
            String[] pattern = scanner.nextLine().split(" ");
            List<Integer> accessList = new ArrayList<>();
            for (String s : pattern) {
                accessList.add(Integer.parseInt(s));
            }
            accessPatterns.put(i, accessList);
        }

        // 创建并启动内存管理系统
        SegmentPageMemoryManagerFIFO memoryManager = new SegmentPageMemoryManagerFIFO(memorySize, blockSize, numBlocks, numProcesses, processSegments, accessPatterns);
        memoryManager.handleRequest();
    }

}
