import { fromUnixTime, format } from 'date-fns'
import jsPDF from 'jspdf'

/**
 * 
 * @param timestamp in milliseconds
 * @param formatStr 
 * @returns 
 */
export const formatTimestamp = (timestamp: number, formatStr: string) => {
  const date = fromUnixTime(timestamp)

  switch (formatStr) {
    case 'mmss':
      return format(date, 'mm:ss')
    case 'hh':
      return format(date, 'HH')
    case 'minutes':
      return format(date, 'HH:mm:ss')
    case 'hours':
      return format(date, 'HH:mm')
    case 'days':
      return format(date, 'dd/MMM HH:mm')
    case 'months':
      return format(date, 'dd/MMM')
    case 'years':
      return format(date, 'MMM/y')
    default:
      return format(date, 'dd/MMM :HH:mm')
  }
}

/**
 * @param canvas HTMLCanvasElement
 * @param filename string
 * @returns void
 * 
 * Takes a canvas element and downloads as PDF
 */
export const downloadCanvas = (canvas: HTMLCanvasElement, filename: string) => {
  const width = canvas.offsetWidth
  const height = canvas.offsetHeight
  const imgData = canvas.toDataURL('image/png')
  const doc = new jsPDF('l', 'pt')
  doc.addImage(imgData, 'PNG', 15, 15, width, height)
  doc.save(`${filename}.pdf`)
}

/**
 * @param page HTMLElement
 * @param canvases HTMLCollectionOf<HTMLCanvasElement>
 * @returns void
 * 
 * Takes an element and a list of canvases within.
 * Loops and downloads to PDF.
 */
export const downloadMultipleCanvases = (page: HTMLElement, canvases: HTMLCollectionOf<HTMLCanvasElement>) => {
  const pageHeight = page.offsetHeight
  const pageWidth = page.offsetWidth

  // create a new canvas object that we will populate with all other canvas objects
  const pageCanvas = document.createElement('canvas')
  pageCanvas.id = 'pagecanvas'
  pageCanvas.width = pageWidth
  pageCanvas.height = pageHeight 

  // keep track canvas position
  const pdfctx = pageCanvas.getContext('2d') as CanvasRenderingContext2D
  const widthBuffer = 20
  let pdfctxX = 0
  let pdfctxY = 0

  for (let i = 0; i < canvases.length; i++) {
    const canvasHeight = canvases[i].offsetHeight
    const canvasWidth = canvases[i].offsetWidth
    
    // draw the canvas into the new canvas
    pdfctx.drawImage(canvases[i], pdfctxX, pdfctxY, canvasWidth, canvasHeight)
    pdfctxX += canvasWidth + widthBuffer
    
    // our page is in a grid pattern so replicate that in the new canvas
    if (i % 2 === 1) {
      pdfctxX = 0;
      pdfctxY += canvasHeight
    }
  }
  
  // create new pdf and add our new canvas as an image
  const pdf = new jsPDF('p', 'pt', [pageWidth, pageHeight])
  pdf.addImage(pageCanvas, 'PNG', 0, 0, pageWidth, pageHeight)
  pdf.save('graphs.pdf')
}
