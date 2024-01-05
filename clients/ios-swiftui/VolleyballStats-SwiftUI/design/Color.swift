//
//  Color.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 04/01/2024.
//

import SwiftUI

extension Color {
    init(
        light lightModeColor: @escaping @autoclosure () -> Color,
        dark darkModeColor: @escaping @autoclosure () -> Color
    ) {
        self.init(
            UIColor(
                light: UIColor(lightModeColor()),
                dark: UIColor(darkModeColor())
            )
        )
    }
    
    init(hex: UInt, alpha: Double = 1) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 08) & 0xff) / 255,
            blue: Double((hex >> 00) & 0xff) / 255,
            opacity: alpha
        )
    }
    
    static var surface: Self {
        Self(
            light: Color(white: 0.95),
            dark: Color(hex: 0xFF2A3038)
        )
    }
    
    static var background: Self {
        Self(
            light: Color.white,
            dark: Color.black
        )
    }
    
    static var primary: Self {
        Self(
            light: Color(hex: 0xFF96490B),
            dark: Color(hex: 0xFFFFB68A)
        )
    }
    
    static var tertiary: Self {
        Self(
            light: Color(hex: 0xFF006878),
            dark: Color(hex: 0xFF53D7F1)
        )
    }
}

extension UIColor {
    convenience init(
        light lightModeColor: @escaping @autoclosure () -> UIColor,
        dark darkModeColor: @escaping @autoclosure () -> UIColor
     ) {
        self.init { traitCollection in
            switch traitCollection.userInterfaceStyle {
            case .light:
                return lightModeColor()
            case .dark, .unspecified:
                return darkModeColor()
            @unknown default:
                return darkModeColor()
            }
        }
    }
}
