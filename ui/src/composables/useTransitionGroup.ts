import gsap from 'gsap'

const useTransitionGroup = () => {
  const onBeforeEnter = (el: any) => {
    el.style.opacity = 0
    el.style.height = 0
  }
  const onEnter = (el: any, done: any) => {
    gsap.to(el, {
      opacity: 1,
      height: '4em',
      delay: el.dataset.index * 0.15,
      onComplete: done
    })
  }
  const onLeave = (el: any, done: any) => {
    gsap.to(el, {
      opacity: 0,
      height: 0,
      delay: el.dataset.index * 0.15,
      onComplete: done
    })
  }

  return { onBeforeEnter, onEnter, onLeave }
}

export default useTransitionGroup