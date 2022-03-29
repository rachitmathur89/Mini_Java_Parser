#include <stdio.h>
#include <stdlib.h>

union ilword {
    int n;
    union ilword* ptr;
    void(*f)();
};
typedef union ilword word;

word param[1];
int next_param = 0;

word r0 = {0};

word vg0 = {0};
word vg1 = {0};
word vg2 = {0};
void INIT();
void MAIN();
void Flow_l1();
void Flow_l2();
void Flow_l3();
void Flow_h();
int main() {
    INIT();
    MAIN();
    return 0;
}

void INIT() {
    word vl[0];
    word r4 = {0};
    word r3 = {0};
    word r2 = {0};
    word r1 = {0};
    int p;
    for(p = 0; p <= -1 && p < 1; p++) {
        vl[p] = param[p];
    }
    next_param = 0;
INIT:
    r1.n = 1;
    r2.n = 0;
    vg0.ptr = calloc(r2.n, sizeof(word));
    r2.n = 0;
    vg1.ptr = calloc(r2.n, sizeof(word));
    r2.n = 4;
    vg2.ptr = calloc(r2.n, sizeof(word));
    r3 = vg2;
    r4.f = &Flow_l1;
    *(r3.ptr) = r4;
    r3.ptr = r3.ptr + r1.n;
    r4.f = &Flow_l2;
    *(r3.ptr) = r4;
    r3.ptr = r3.ptr + r1.n;
    r4.f = &Flow_l3;
    *(r3.ptr) = r4;
    r3.ptr = r3.ptr + r1.n;
    r4.f = &Flow_h;
    *(r3.ptr) = r4;
    return;
}

void MAIN() {
    word vl[0];
    word r1 = {0};
    int p;
    for(p = 0; p <= -1 && p < 1; p++) {
        vl[p] = param[p];
    }
    next_param = 0;
MAIN:
    r1.n = 0;
    printf("%d\n", r1.n);
    return;
}

void Flow_l1() {
    word vl[0];
    int p;
    for(p = 0; p <= -1 && p < 1; p++) {
        vl[p] = param[p];
    }
    next_param = 0;
Flow_l1:
    r0.n = 0;
    return;
}

void Flow_l2() {
    word vl[0];
    int p;
    for(p = 0; p <= -1 && p < 1; p++) {
        vl[p] = param[p];
    }
    next_param = 0;
Flow_l2:
    r0.n = 0;
    return;
}

void Flow_l3() {
    word vl[0];
    int p;
    for(p = 0; p <= -1 && p < 1; p++) {
        vl[p] = param[p];
    }
    next_param = 0;
Flow_l3:
    r0.n = 0;
    return;
}

void Flow_h() {
    word vl[0];
    int p;
    for(p = 0; p <= -1 && p < 1; p++) {
        vl[p] = param[p];
    }
    next_param = 0;
Flow_h:
    r0.n = 0;
    return;
}

