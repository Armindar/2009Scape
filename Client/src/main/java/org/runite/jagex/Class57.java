package org.runite.jagex;

final class Class57 {

   static Class3_Sub28_Sub16 aClass3_Sub28_Sub16_895;
   int anInt896 = 0;
   static int[] anIntArray898 = new int[2500];
   int anInt899 = 2048;
   static int anInt900;
   static int anInt902 = 100;
   static int anInt903;
   static int[] anIntArray904 = new int[200];
   static int activeWorldListSize;
   int anInt907 = 0;
   int anInt908 = 2048;


   final void method1190(int var1, RSByteBuffer var2, int var3) {
      try {
         while(true) {
            int var4 = var2.getByte((byte)-88);
            if(var4 == 0) {
               ;
               return;
            }

            this.method1191(var4, var2, var3, true);
         }
      } catch (RuntimeException var5) {
         throw Class44.method1067(var5, "ic.A(" + var1 + ',' + (var2 != null?"{...}":"null") + ',' + var3 + ')');
      }
   }

   private final void method1191(int var1, RSByteBuffer var2, int var3, boolean var4) {
      try {
         if(1 == var1) {
            this.anInt896 = var2.getByte((byte)-78);
         } else {
            if(var1 == 2) {
               this.anInt908 = var2.getShort(1);
            } else if(var1 == 3) {
               this.anInt899 = var2.getShort(1);
            } else if(4 == var1) {
               this.anInt907 = var2.getShort((byte)46);
            }
         }

         if(!var4) {
            method1192((byte)123);
         }

      } catch (RuntimeException var6) {
         throw Class44.method1067(var6, "ic.C(" + var1 + ',' + (var2 != null?"{...}":"null") + ',' + var3 + ',' + var4 + ')');
      }
   }

   public static void method1192(byte var0) {
      try {
         anIntArray898 = null;
         anIntArray904 = null;
         if(var0 == -86) {
            aClass3_Sub28_Sub16_895 = null;
         }
      } catch (RuntimeException var2) {
         throw Class44.method1067(var2, "ic.B(" + var0 + ')');
      }
   }

}
