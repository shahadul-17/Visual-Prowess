#include "socket.h"

int initializeServerSocket(USHORT port, SOCKET *serverSocket)
{
    int errorCode = 0;

    struct sockaddr_in hints;

    WSADATA wsaData;

    if ((errorCode = WSAStartup(MAKEWORD(2, 2), &wsaData)) == 0)
    {
        memset(&hints, 0, sizeof(hints));

        if ((*serverSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == INVALID_SOCKET)
        {
            errorCode = WSAGetLastError();

            WSACleanup();
        }
        else
        {
            hints.sin_family = AF_INET;
            hints.sin_addr.s_addr = INADDR_ANY;
            hints.sin_port = htons(port);

            if ((errorCode = bind(*serverSocket, (struct sockaddr *)&hints, sizeof(hints))) != 0)
            {
                closesocket(*serverSocket);
                WSACleanup();
            }
        }
    }

    return errorCode;
}

int read(char buffer[], int bufferLength, int timeoutSeconds, int timeoutMicroseconds, int *endpointAddressLength, struct sockaddr_in *endpointAddress, SOCKET serverSocket)
{
    struct timeval _timeval;

    fd_set fileDescriptorSet;

    FD_ZERO(&fileDescriptorSet);
    FD_SET(serverSocket, &fileDescriptorSet);

    _timeval.tv_sec = timeoutSeconds;
    _timeval.tv_usec = timeoutMicroseconds;

    if (select(0, &fileDescriptorSet, 0, 0, &_timeval) > 0)
    {
        return recvfrom(serverSocket, buffer, bufferLength, 0, (struct sockaddr *)endpointAddress, endpointAddressLength);
    }

    return SOCKET_ERROR;
}

int write(char buffer[], int bufferLength, int endpointAddressLength, struct sockaddr_in *endpointAddress, SOCKET serverSocket)
{
    return sendto(serverSocket, buffer, bufferLength, 0, (struct sockaddr*)endpointAddress, endpointAddressLength);
}
