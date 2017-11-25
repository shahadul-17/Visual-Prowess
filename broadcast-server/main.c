#include <pthread.h>
#include <stdio.h>

#include "utility.h"
#include "socket.h"

static int index = 0, endpointAddressLength = 0, numberOfNodes = 0;

static struct Node *nodes;

static SOCKET serverSocket = INVALID_SOCKET;

void initializeBufferMatrix(struct Node *node)
{
    int i = 0;

    (*node).bufferMatrix.index = 0;

    while (i < BUFFER_MATRIX_WIDTH)
    {
        (*node).bufferMatrix.bytesRead[i] = 0;

        i++;
    }
}

void increaseReceiverAddresses(int index)
{
    if (nodes[index].numberOfReceiverAddresses == 0)
    {
        nodes[index].numberOfReceiverAddresses += DYNAMIC_ARRAY_INCREMENT;
        nodes[index].receiverAddresses = (struct sockaddr_in *)malloc(sizeof(struct sockaddr_in) * nodes[index].numberOfReceiverAddresses);
    }
    else
    {
        int i = 0;

        struct sockaddr_in *tempReceiverAddresses = (struct sockaddr_in *)malloc(sizeof(struct sockaddr_in) * nodes[index].numberOfReceiverAddresses);

        while (i < nodes[index].numberOfReceiverAddresses)
        {
            tempReceiverAddresses[i] = nodes[index].receiverAddresses[i];
            i++;
        }

        i--;

        free(nodes[index].receiverAddresses);

        nodes[index].numberOfReceiverAddresses += DYNAMIC_ARRAY_INCREMENT;
        nodes[index].receiverAddresses = (struct sockaddr_in *)malloc(sizeof(struct sockaddr_in) * nodes[index].numberOfReceiverAddresses);

        while (i >= 0)
        {
            nodes[index].receiverAddresses[i] = tempReceiverAddresses[i];
            i--;
        }

        free(tempReceiverAddresses);
    }
}

void increaseNodes()
{
    if (numberOfNodes == 0)
    {
        numberOfNodes += DYNAMIC_ARRAY_INCREMENT;
        nodes = (struct Node *)malloc(sizeof(struct Node) * numberOfNodes);
    }
    else
    {
        int i = 0;

        struct Node *tempNodes = (struct Node *)malloc(sizeof(struct Node) * numberOfNodes);

        while (i < numberOfNodes)
        {
            tempNodes[i] = nodes[i];
            i++;
        }

        i--;

        free(nodes);

        numberOfNodes += DYNAMIC_ARRAY_INCREMENT;
        nodes = (struct Node *)malloc(sizeof(struct Node) * numberOfNodes);

        while (i >= 0)
        {
            nodes[i] = tempNodes[i];
            i--;
        }

        free(tempNodes);
    }
}

void *handleClients(void *argument)
{
    int i = 0;

    struct Node *node = &(nodes[(int)argument]);

    while (1)
    {
        if ((*node).bufferMatrix.index == BUFFER_MATRIX_WIDTH)
        {
            (*node).bufferMatrix.index = 0;
        }

        if ((*node).bufferMatrix.bytesRead[(*node).bufferMatrix.index] > 0)
        {
            for (i = 0; i < (*node).index; i++)
            {
                write((*node).bufferMatrix.buffers[(*node).bufferMatrix.index], (*node).bufferMatrix.bytesRead[(*node).bufferMatrix.index], endpointAddressLength, &((*node).receiverAddresses[i]), serverSocket);

                printf("sent to %s:%d\n\n", inet_ntoa((*node).receiverAddresses[i].sin_addr), (*node).receiverAddresses[i].sin_port);
            }

            (*node).bufferMatrix.bytesRead[(*node).bufferMatrix.index] = 0;
        }

        (*node).bufferMatrix.index++;
    }

    return NULL;
}

void insertNode(struct sockaddr_in *transmitterAddress)
{
    pthread_t thread;

    if (index == numberOfNodes)
    {
        increaseNodes();
    }

    nodes[index].index = nodes[index].numberOfReceiverAddresses = 0;

    if (pthread_create(&thread, NULL, handleClients, (void *)index) != 0)
    {
        printf("error: thread creation failed...\n\n");
    }

    nodes[index].bufferMatrix.buffers[0][0] = index + '0';     // temporarily using the buffer of the node...

    write(nodes[index].bufferMatrix.buffers[0], 1, endpointAddressLength, transmitterAddress, serverSocket);

    index++;
}

int getNextBufferMatrixIndex(struct Node node)
{
    int index = node.bufferMatrix.index + 1;

    if (index >=  BUFFER_MATRIX_WIDTH)
    {
        index = 0;
    }

    return index;
}

int main()
{
    char tempCommand[5], buffer[BUFFER_MATRIX_HEIGHT], *commands[] = { "recv", "rest", "stop", "tran" };
    int tempIndex = -1, indexBufferMatrix = 0, bytesRead = 0;

    struct sockaddr_in endpointAddress;

    endpointAddressLength = sizeof(endpointAddress);

    if (initializeServerSocket(PORT, &serverSocket) == 0)
    {
        printf("waiting for data...\n\n");

        while (1)
        {
            if ((bytesRead = read(buffer, BUFFER_MATRIX_HEIGHT, TIMEOUT, 0, &endpointAddressLength, &endpointAddress, serverSocket)) != SOCKET_ERROR)
            {
                tempIndex = buffer[0] - '0';

                if (bytesRead == 5)
                {
                    printf("BYTES READ = %d\n\n", bytesRead);

                    buffer[bytesRead] = '\0';

                    memcpy(tempCommand, buffer + 1, bytesRead);     // copying command from buffer...

                    printf("%s\n\n", tempCommand);

                    if (strcmp(tempCommand, commands[0]) == 0)
                    {
                        if (nodes[tempIndex].index == nodes[tempIndex].numberOfReceiverAddresses)
                        {
                            increaseReceiverAddresses(tempIndex);
                        }

                        nodes[tempIndex].receiverAddresses[nodes[tempIndex].index] = endpointAddress;
                        nodes[tempIndex].index++;
                    }
                    else if (strcmp(tempCommand, commands[1]) == 0)
                    {
                        indexBufferMatrix = getNextBufferMatrixIndex(nodes[tempIndex]);

                        memcpy(nodes[tempIndex].bufferMatrix.buffers[indexBufferMatrix], tempCommand, 4);
                        nodes[tempIndex].bufferMatrix.bytesRead[indexBufferMatrix] = 4;
                    }
                    else if (strcmp(tempCommand, commands[2]) == 0)
                    {
                        printf("INSIDE %d == %s = %d\n\n", tempIndex, commands[2], strlen(commands[2]));

                        return -1;
                    }
                    else if (strcmp(tempCommand, commands[3]) == 0)
                    {
                        insertNode(&endpointAddress);
                    }
                }
                else
                {
                    indexBufferMatrix = getNextBufferMatrixIndex(nodes[tempIndex]);

                    memcpy(nodes[tempIndex].bufferMatrix.buffers[indexBufferMatrix], buffer + 1, bytesRead - 1);

                    nodes[tempIndex].bufferMatrix.bytesRead[indexBufferMatrix] = bytesRead - 1;
                }
            }
        }
    }
    else
    {
        printf("\nerror: unable to start server at port: %d...\n\n", PORT);
    }

    return 0;
}
