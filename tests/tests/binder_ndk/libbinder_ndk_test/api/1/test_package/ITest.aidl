package test_package;
interface ITest {
  String GetName();
  void TestVoidReturn();
  oneway void TestOneway();
  int GiveMeMyCallingPid();
  int GiveMeMyCallingUid();
  oneway void CacheCallingInfoFromOneway();
  int GiveMeMyCallingPidFromOneway();
  int GiveMeMyCallingUidFromOneway();
  int RepeatInt(int value);
  long RepeatLong(long value);
  float RepeatFloat(float value);
  double RepeatDouble(double value);
  boolean RepeatBoolean(boolean value);
  char RepeatChar(char value);
  byte RepeatByte(byte value);
  IBinder RepeatBinder(IBinder value);
  @nullable IBinder RepeatNullableBinder(@nullable IBinder value);
  test_package.IEmpty RepeatInterface(test_package.IEmpty value);
  @nullable test_package.IEmpty RepeatNullableInterface(@nullable test_package.IEmpty value);
  ParcelFileDescriptor RepeatFd(in ParcelFileDescriptor fd);
  @nullable ParcelFileDescriptor RepeatNullableFd(in @nullable ParcelFileDescriptor fd);
  String RepeatString(String value);
  @nullable String RepeatNullableString(@nullable String value);
  test_package.RegularPolygon RepeatPolygon(in test_package.RegularPolygon value);
  void RenamePolygon(inout test_package.RegularPolygon value, String newName);
  boolean[] RepeatBooleanArray(in boolean[] input, out boolean[] repeated);
  byte[] RepeatByteArray(in byte[] input, out byte[] repeated);
  char[] RepeatCharArray(in char[] input, out char[] repeated);
  int[] RepeatIntArray(in int[] input, out int[] repeated);
  long[] RepeatLongArray(in long[] input, out long[] repeated);
  float[] RepeatFloatArray(in float[] input, out float[] repeated);
  double[] RepeatDoubleArray(in double[] input, out double[] repeated);
  String[] RepeatStringArray(in String[] input, out String[] repeated);
  test_package.RegularPolygon[] RepeatRegularPolygonArray(in test_package.RegularPolygon[] input, out test_package.RegularPolygon[] repeated);
  @nullable boolean[] RepeatNullableBooleanArray(in @nullable boolean[] input);
  @nullable byte[] RepeatNullableByteArray(in @nullable byte[] input);
  @nullable char[] RepeatNullableCharArray(in @nullable char[] input);
  @nullable int[] RepeatNullableIntArray(in @nullable int[] input);
  @nullable long[] RepeatNullableLongArray(in @nullable long[] input);
  @nullable float[] RepeatNullableFloatArray(in @nullable float[] input);
  @nullable double[] RepeatNullableDoubleArray(in @nullable double[] input);
  @nullable String[] RepeatNullableStringArray(in @nullable String[] input);
  @nullable String[] DoubleRepeatNullableStringArray(in @nullable String[] input, out @nullable String[] repeated);
  test_package.Foo repeatFoo(in test_package.Foo inFoo);
  void renameFoo(inout test_package.Foo foo, String name);
  void renameBar(inout test_package.Foo foo, String name);
  int getF(in test_package.Foo foo);
  const int kZero = 0;
  const int kOne = 1;
  const int kOnes = -1;
  const String kEmpty = "";
  const String kFoo = "foo";
}
