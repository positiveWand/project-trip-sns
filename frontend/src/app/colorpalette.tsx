import { cn } from "@/lib/utils"

function ColorPalette() {
  const colors = [
    'background',
    'foreground',
    'card',
    'popover',
    'primary',
    'secondary',
    'muted',
    'accent',
    'destructive',
    'low',
    'medium',
    'high',
    'border',
    'input',
    'ring',
    'chart-1',
    'chart-2',
    'chart-3',
    'chart-4',
    'chart-5',
  ]

  const backgroundColor = [
    'bg-background',
    'bg-foreground',
    'bg-card',
    'bg-popover',
    'bg-primary',
    'bg-secondary',
    'bg-muted',
    'bg-accent',
    'bg-destructive',
    'bg-low',
    'bg-medium',
    'bg-high',
    'bg-border',
    'bg-input',
    'bg-ring',
    'bg-chart-1',
    'bg-chart-2',
    'bg-chart-3',
    'bg-chart-4',
    'bg-chart-5',
  ]

  return (
    <div className='flex flex-col py-10 px-32 text-center'>
      <h1 className="scroll-m-20 text-4xl font-extrabold tracking-tight lg:text-5xl">
        Color Palette
      </h1>
      <div className='flex flex-wrap'>
        {
          colors.map((color, index) => (
            <div>
              <span>{color}</span>
              <div className={cn('w-52 h-52 m-1', backgroundColor[index])}></div>
            </div>
          ))
        }
      </div>
    </div>
  )
}

export default ColorPalette
