.source "T_if_gt_10.java"
.class  public Ldot/junit/opcodes/if_gt/d/T_if_gt_10;
.super  Ljava/lang/Object;


.method public constructor <init>()V
.registers 1

       invoke-direct {v0}, Ljava/lang/Object;-><init>()V
       return-void
.end method

.method public run(II)Z
.registers 8

       if-gt v6, v7, :Label11
       const/4 v6, 0
       return v6

:Label11
       const v6, 0
       nop
       return v6
.end method
