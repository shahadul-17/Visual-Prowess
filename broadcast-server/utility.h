#ifndef UTILITY_H
#define UTILITY_H

#include <string.h>

#define BUFFER_MATRIX_WIDTH 8
#define BUFFER_MATRIX_HEIGHT 1024
#define DYNAMIC_ARRAY_INCREMENT 5
#define PORT 60499
#define TIMEOUT 10      // 10 seconds timeout...

struct BufferMatrix
{
    char buffers[BUFFER_MATRIX_WIDTH][BUFFER_MATRIX_HEIGHT];
    int index, bytesRead[BUFFER_MATRIX_HEIGHT];
};

struct Node
{
    int index, numberOfReceiverAddresses;

    struct BufferMatrix bufferMatrix;
    struct sockaddr_in *receiverAddresses;
};

#endif
