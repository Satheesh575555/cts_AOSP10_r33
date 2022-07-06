# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

.source "T_sget_wide_12.java"
.class  public Ldot/junit/opcodes/sget_wide/d/T_sget_wide_12;
.super  Ldot/junit/opcodes/sget_wide/d/T_sget_wide_1;


.method public constructor <init>()V
.registers 1

       invoke-direct {v0}, Ldot/junit/opcodes/sget_wide/d/T_sget_wide_1;-><init>()V
       return-void
.end method

.method public run()J
.registers 3

       sget-wide v1, Ldot/junit/opcodes/sget_wide/d/T_sget_wide_1;->pvt1:J
       return-wide v1
.end method

