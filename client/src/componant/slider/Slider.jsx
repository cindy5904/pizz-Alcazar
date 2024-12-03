import React from "react";
import Slider from "react-slick";
import "./slider.css";
import pizza1 from "../../assets/images/baseCreme.webp";
import pizza2 from "../../assets/images/baseTomate.jpg";
import pizza3 from "../../assets/images/buffala.webp";
import pizza4 from "../../assets/images/fromagi.webp";

const images = [
  {
    src: pizza1,
    alt: "Pizza Margherita",
    
  },
  {
    src: pizza2,
    alt: "Pizza Pepperoni",
    
  },
  {
    src: pizza3,
    alt: "Pizza Végétarienne",
    
  },
  {
    src: pizza4,
    alt: "Pizza Végétarienne",
    
  }
];

const ImageSlider = () => {
  const settings = {
    dots: true, 
    infinite: true, 
    speed: 500, 
    slidesToShow: 1, 
    slidesToScroll: 1, 
    autoplay: true, 
    autoplaySpeed: 3000, 
    arrows: true,
    centerMode: true, 
    centerPadding: "0px",
  };

  return (
    <div className="slider-container">
      <Slider {...settings}>
        {images.map((image, index) => (
          <div key={index} className="slider-item">
            <img src={image.src} alt={image.alt} />
            <p className="slider-caption">{image.caption}</p>
          </div>
        ))}
       
      </Slider>
    </div>
  );
};

export default ImageSlider;
