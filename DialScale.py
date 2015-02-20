bl_info = {
    "name": "Dial and Scale",
    "author": "stacker",
    "version": (1, 1),
    "blender": (2, 70, 0),
    "location": "3DView > Add > Curve > Dial and Scale",
    "description": "Add an array of text number objects or watch dials.",
    "warning": "",
    "wiki_url": "https://github.com/3dbug/blender/blob/master/DialScale.py",
    "tracker_url": "https://github.com/3dbug/blender/issues",
    "category": "Add Curve"}

import bpy,math
import mathutils

from bpy.props import IntProperty,FloatProperty,StringProperty,EnumProperty,BoolProperty

class DialScale(bpy.types.Operator):
    """ Creates an array of text elements"""
    bl_idname = "curve.dial_scale"
    bl_label = "Create Dials and Scales"
    bl_options = {'REGISTER', 'UNDO'}

    start = IntProperty(name="Start",description="Start value",min=0, max=100,default=1 )
    count = IntProperty(name="Count",description="Number of items to create",min=1, max=100, default=12  )
    step  = IntProperty(name="Step",description="Increment of number",min=1, max=10000, default=1  ) 
    offset = FloatProperty(name="Offset",description="Distance",min=0.01, max=100.0, default=1.0 )
    circular = BoolProperty(name="Circular",description="Rotate Elements",default=False)    
    rotate = FloatProperty(name="Rotation",description="Start rotation of first item",min=0.01, max=360.0, default=0.0 )
    segment = FloatProperty(name="Segment",description="Circle Segment",min=0.0, max=360.0, default=360.0 )
    ticks = IntProperty(name="Ticks",description="Number of ticks between numbers",min=0, max=100, default=1  ) 

    all_fonts = []
    for afont in bpy.data.fonts:
        all_fonts.append(( afont.name, afont.name,""))
    if len(all_fonts) == 0:
        all_fonts.append(("Bfont","Bfont",""))
    font = EnumProperty( name="Fonts",items=all_fonts )

    def execute(self, context):
        x = -self.offset
        y = 0.0
        angle = 2*math.pi+math.pi/4 - math.radians( self.rotate )
        angle_step = math.radians( self.segment ) / self.count
        angle = angle - angle_step
        pos = self.start
        num = self.start
        end = self.count + self.start
        if len(bpy.data.fonts) == 0:
            # if no fonts exist we add and delete a text object to initiate it
            bpy.ops.object.text_add()
            context.scene.objects.unlink(bpy.data.objects['Text'])
            bpy.data.objects.remove(bpy.data.objects['Text'])
        font_obj = bpy.data.fonts[ self.font ]
        bpy.context.space_data.pivot_point = 'ACTIVE_ELEMENT'
    
        while pos < end:
            if self.circular:
                vec3d = mathutils.Vector((self.offset, 0, 0))
                vpos = vec3d * mathutils.Matrix.Rotation( -angle , 3, 'Z')
            else:
                x = x + self.offset
                vpos=(x,0,0)
            angle = angle - angle_step

            bpy.ops.object.text_add(location=(0,0, 0))
            #bpy.ops.object.origin_set(type='ORIGIN_GEOMETRY')
            bpy.ops.transform.translate(value=vpos)

            ob=bpy.context.object
            ob.data.body = str(num)
            ob.data.font = font_obj
            for t in range(0,self.ticks):
                tick_step = angle_step / self.ticks
                vec3d = mathutils.Vector((self.offset*1.3, 0, 0))
                vrot = vec3d * mathutils.Matrix.Rotation( -(angle + t*tick_step) , 3, 'Z')
                bpy.ops.mesh.primitive_plane_add(radius=.04 if t == 0 else .02, location=(0,0,0))
                bpy.ops.transform.resize(value=(6,1,1))
                bpy.ops.transform.rotate(value= angle + t*tick_step, axis=(0, 0, 1))
                bpy.ops.transform.translate(value=vrot)

            pos = pos + 1
            num = num + self.step
        return {'FINISHED'}

def menu_func(self, context):
    self.layout.operator(DialScale.bl_idname, icon='PLUGIN')

def register():
    bpy.utils.register_class(DialScale)
    bpy.types.INFO_MT_curve_add.append(menu_func)

def unregister():
    bpy.utils.unregister_class(DialScale)
    bpy.types.INFO_MT_curve_add.remove(menu_func)

if __name__ == "__main__":
    register()
