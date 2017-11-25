#ifndef SOCKET_H
#define SOCKET_H

#define _WIN32_WINNT 0x0501

#include <winsock2.h>
#include <ws2tcpip.h>

int initializeServerSocket(USHORT, SOCKET *);
int read(char [], int, int, int, int *, struct sockaddr_in *, SOCKET);
int write(char [], int, int, struct sockaddr_in *, SOCKET);

#endif
