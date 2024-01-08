//
//  Color.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 04/01/2024.
//

import SwiftUI
import shared

extension Color {
    private init(
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
    
    private init(hex: Int64, alpha: Double = 1) {
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
            dark: Color(hex: ColorValues.shared.Grey)
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
            light: Color(hex: ColorValues.shared.LightPrimary),
            dark: Color(hex: ColorValues.shared.DarkPrimary)
        )
    }
    
    static var primaryContainer: Self {
        Self(
            light: Color(hex: ColorValues.shared.LightPrimaryContainer),
            dark: Color(hex: ColorValues.shared.DarkPrimaryContainer)
        )
    }
    
    static var tertiary: Self {
        Self(
            light: Color(hex: ColorValues.shared.LightTertiary),
            dark: Color(hex: ColorValues.shared.DarkTertiary)
        )
    }
    
    static var tertiaryContainer: Self {
        Self(
            light: Color(hex: ColorValues.shared.LightTertiaryContainer),
            dark: Color(hex: ColorValues.shared.DarkTertiaryContainer)
        )
    }
    
    static var onBackground: Self {
        Self(
            light: Color(hex: ColorValues.shared.LightOnBackground),
            dark: Color.white
        )
    }
}

extension UIColor {
    fileprivate convenience init(
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
